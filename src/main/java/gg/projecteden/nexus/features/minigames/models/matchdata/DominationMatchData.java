package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.Domination;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import lombok.Data;
import org.bukkit.Location;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@MatchDataFor(Domination.class)
public class DominationMatchData extends MatchData {
	private List<Point> points = new ArrayList<>();
	private static final int CAPTURE_THRESHOLD = 30;

	public DominationMatchData(Match match) {
		super(match);
	}

	@Data
	public class Point {
		private String id;
		private Team ownerTeam;
		private int captureProgress;

		public Point(String id) {
			this.id = id;
			getRegion();
		}

		private void getProgressBar() {
			// TODO Color
			StringUtils.progressBar(captureProgress, CAPTURE_THRESHOLD, ProgressBarStyle.NONE, CAPTURE_THRESHOLD);
		}

		public void updateHologram() {
			if (isCaptured()) {
				// line 1 -> team colored texture pack emoji
			} else {
				// line 1 -> white texture pack emoji
			}

			// line 2 -> progressbar
		}

		public void contestedHologram() {
			// yellow progress bar? full?
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
			Map<Team, List<Minigamers>> teams = getTeams();

			if (isContested()) {
				contestedHologram();
			} else if (isBeingCaptured()) {
				Team team = teams.iterator().next();
				if (team.equals(ownerTeam)) {
					if (captureProgress > 0)
						captureProgress -= teams.get(team).size();
				} else {
					captureProgress += teams.get(team).size();
					if (captureProgress >= CAPTURE_THRESHOLD) {
						ownerTeam = team;
						captureProgress = 0;
						match.broadcast(team.getColoredName() + " &3has captured &e" + id.toUpperCase() + "&3!");
						// TODO sound
					}
				}
				updateHologram();
			}

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
				.get();
		}

		public Map<Team, List<Minigamer>> getTeams() {
			return new HashSet<>() {{
				for (Minigamer minigamer : getMinigamers())
					computeIfAbsent(minigamer.getTeam(), $ -> new ArrayList<>()).add(minigamer);
			}}
		}

	}

}
