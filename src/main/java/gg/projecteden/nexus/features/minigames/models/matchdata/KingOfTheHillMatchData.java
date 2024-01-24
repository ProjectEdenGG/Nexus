package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.minigames.Minigames;
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
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.line.Offset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@Data
@MatchDataFor(KingOfTheHill.class)
public class KingOfTheHillMatchData extends MatchData {
	private Point activePoint;
	private List<Point> points = new ArrayList<>();
	private Iterator<Point> pointIterator;
	private Map<String, CustomCharacter> characterCache = new HashMap<>();

	public KingOfTheHillMatchData(Match match) {
		super(match);
	}

	public void shufflePoints() {
		Collections.shuffle(points);
	}

	public void movePoint(boolean broadcast) {
		if (pointIterator == null || !pointIterator.hasNext())
			pointIterator = points.iterator();

		activePoint = pointIterator.next();
		points.forEach(Point::hologram);
		if (broadcast)
			match.broadcast("The capture point has moved!");
	}

	@Data
	public class Point {
		private final String id;

		public Point(String id) {
			this.id = id;
			getRegion();
		}

		public void hologram() {
			if (this != activePoint) {
				setIcon("");
				setText("");
			} else if (isContested()) {
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
			if (isNullOrEmpty(line))
				HologramsAPI.byId(Minigames.getWorld(), "%s_point_%s_text".formatted(arena.getRegionBaseName(), id)).setLine(0, Offset.text());
			else
				HologramsAPI.byId(Minigames.getWorld(), "%s_point_%s_text".formatted(arena.getRegionBaseName(), id)).setLine(0, line);
		}

		private void setIcon(String color) {
			if (isNullOrEmpty(color))
				HologramsAPI.byId(Minigames.getWorld(), "%s_point_%s_icon".formatted(arena.getRegionBaseName(), id)).setLine(0, Offset.text());
			else
				HologramsAPI.byId(Minigames.getWorld(), "%s_point_%s_icon".formatted(arena.getRegionBaseName(), id)).setLine(0, "%s%s".formatted(color, getCharacter().getChar()));
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

			if (this != activePoint)
				return;

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
