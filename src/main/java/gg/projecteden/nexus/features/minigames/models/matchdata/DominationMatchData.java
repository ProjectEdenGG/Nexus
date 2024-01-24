package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.mechanics.Domination;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.files.FontFile.CustomCharacter;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar.SummaryStyle;
import lombok.Data;
import tech.blastmc.holograms.api.HologramsAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@MatchDataFor(Domination.class)
public class DominationMatchData extends MatchData {
	private List<Point> points = new ArrayList<>();
	private static final int CAPTURE_THRESHOLD = 15;
	private Map<String, CustomCharacter> characterCache = new HashMap<>();

	public DominationMatchData(Match match) {
		super(match);
	}

	@Data
	public class Point {
		private final String id;
		private Team ownerTeam;
		private Team progressTeam;
		private int captureProgress;

		public Point(String id) {
			this.id = id;
			getRegion();
		}

		public void hologram() {
			if (isContested()) {
				setIcon("&6");
				setProgress("&6Contested");
			} else {
				if (isCaptured())
					setIcon(ownerTeam.getChatColor().toString());
				else
					setIcon("&f");

				if (captureProgress > 0)
					setProgress(ProgressBar.builder()
						.progress(captureProgress)
						.goal(CAPTURE_THRESHOLD)
						.summaryStyle(SummaryStyle.NONE)
						.length(CAPTURE_THRESHOLD * 10)
						.color(progressTeam.getChatColor())
						.seamless(true)
						.build());
				else if (isCaptured())
					setProgress(ownerTeam.getChatColor() + "Captured");
				else
					setProgress("&fCapture");
			}
		}

		private void captureHologram() {
			setIcon("&f");
			setProgress("&fCapture");
		}

		private void setProgress(String line) {
			HologramsAPI.byId(Minigames.getWorld(), "%s_point_%s_text".formatted(arena.getRegionBaseName(), id)).setLine(0, line);
		}

		private void setIcon(String color) {
			HologramsAPI.byId(Minigames.getWorld(), "%s_point_%s_icon".formatted(arena.getRegionBaseName(), id)).setLine(0, "%s%s".formatted(color, getCharacter().getChar()));
		}

		private CustomCharacter getCharacter() {
			return characterCache.computeIfAbsent("point_" + id, id -> ResourcePack.getFontFile().get(id));
		}

		public boolean isContested() {
			return getTeams().keySet().size() > 1;
		}

		public boolean isBeingCaptured() {
			return getTeams().keySet().size() == 1;
		}

		public boolean isCaptured() {
			return ownerTeam != null;
		}

		public void tick() {
			Map<Team, List<Minigamer>> teams = getTeams();

			if (isBeingCaptured()) {
				Team team = teams.keySet().iterator().next();
				if (team.equals(ownerTeam)) {
					if (captureProgress > 0)
						captureProgress = Math.max(captureProgress - teams.get(team).size(), 0);
				} else if (progressTeam != null && !team.equals(progressTeam)) {
					if (captureProgress > 0)
						captureProgress = Math.max(captureProgress - teams.get(team).size(), 0);

					if (captureProgress == 0)
						progressTeam = null;
				} else {
					if (captureProgress == 0)
						progressTeam = team;
				}

				if (team.equals(progressTeam)) {
					progressTeam = team;
					captureProgress = Math.min(captureProgress + teams.get(team).size(), CAPTURE_THRESHOLD);
					if (captureProgress == CAPTURE_THRESHOLD) {
						// TODO Contributor score?
						ownerTeam = team;
						progressTeam = null;
						captureProgress = 0;
						match.broadcast(team.getColoredName() + " &3has captured &e" + id.toUpperCase() + "&3!");
						// TODO sound
					}
				}
			}

			hologram();

			scored();

			// TODO actionbar/subtitle
		}

		private void scored() {
			if (isCaptured())
				match.scored(ownerTeam);
		}

		public ProtectedRegion getRegion() {
			return arena.getProtectedRegion("point_" + id);
		}

		public List<Minigamer> getMinigamers() {
			return OnlinePlayers.where()
				.region(getRegion())
				.world(arena.getWorld())
				.filter(player -> Minigamer.of(player).isPlaying(Domination.class))
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
