package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.minigames.mechanics.KingOfTheHill;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.files.FontFile.CustomCharacter;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@MatchDataFor(KingOfTheHill.class)
public class KingOfTheHillMatchData extends MatchData {
	private List<Point> points = new ArrayList<>();
	private Map<String, CustomCharacter> characterCache = new HashMap<>();

	public KingOfTheHillMatchData(Match match) {
		super(match);
	}

	@Data
	public class Point {
		private final String id;

		public Point(String id) {
			this.id = id;
			getRegion();
		}

		public void hologram() {
			if (isContested()) {
				setIcon("&6");
				setText("&6Contested");
			} else {
				if (isBeingDefended()) {
					setIcon(getDefendingTeam().getChatColor().toString());
					setText(getDefendingTeam().getChatColor() + "Captured");
				} else {
					setIcon("&f");
					setText("&fCapture");
				}
			}
		}

		private void setText(String line) {
			PlayerUtils.runCommandAsConsole("hd setline %s_point_%s_text 1 %s".formatted(arena.getRegionBaseName(), id, line));
		}

		private void setIcon(String color) {
			PlayerUtils.runCommandAsConsole("hd setline %s_point_%s_icon 1 %s%s".formatted(arena.getRegionBaseName(), id, color, getCharacter().getChar()));
		}

		private CustomCharacter getCharacter() {
			return characterCache.computeIfAbsent("point_" + id, id -> ResourcePack.getFontFile().get(id));
		}

		public boolean isContested() {
			return getTeams().keySet().size() > 1;
		}

		public boolean isBeingDefended() {
			return getTeams().keySet().size() == 1;
		}

		public Team getDefendingTeam() {
			return getTeams().keySet().iterator().next();
		}

		public void tick() {
			hologram();

			if (isBeingDefended())
				scored();

			// TODO actionbar/subtitle
		}

		private void scored() {
			if (isBeingDefended())
				match.scored(getDefendingTeam());
		}

		public ProtectedRegion getRegion() {
			return arena.getProtectedRegion("point_" + id);
		}

		public List<Minigamer> getMinigamers() {
			return OnlinePlayers.where()
				.region(getRegion())
				.world(arena.getWorld())
				.filter(player -> Minigamer.of(player).isPlaying(KingOfTheHill.class))
				.map(Minigamer::of);
		}

		public Map<Team, List<Minigamer>> getTeams() {
			return new HashMap<>() {{
				for (Minigamer minigamer : getMinigamers())
					computeIfAbsent(minigamer.getTeam(), $ -> new ArrayList<>()).add(minigamer);
			}};
		}

	}

}
