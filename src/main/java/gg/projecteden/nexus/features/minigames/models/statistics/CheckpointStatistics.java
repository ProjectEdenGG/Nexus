package gg.projecteden.nexus.features.minigames.models.statistics;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.models.checkpoint.CheckpointService;
import gg.projecteden.nexus.models.checkpoint.CheckpointUser;
import gg.projecteden.nexus.models.checkpoint.RecordTotalTime;
import gg.projecteden.nexus.models.minigamestats.MinigameStatsService.LeaderboardRanking;
import gg.projecteden.nexus.utils.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CheckpointStatistics extends MatchStatistics {

	public CheckpointStatistics(MechanicType type, Match match) {
		super(type, match);
	}

	@Override
	public List<MinigameStatistic> getStatistics() {
		List<MinigameStatistic> stats = new ArrayList<>();
		for (Arena arena : ArenaManager.getAllEnabled(mechanic))
			stats.add(new MinigameStatistic(arena.getName(), arena.getDisplayName()) {
				@Override
				public Object format(long score) {
					return StringUtils.getTimeFormat(Duration.ofMillis(score));
				}
			});
		stats.addAll(List.of(GAMES_PLAYED, WINS, TIME_PLAYED));
		return stats;
	}

	@Override
	public List<LeaderboardRanking> getLeaderboard(MinigameStatistic statistic, LocalDateTime after) {
		List<RecordTotalTime> list = new CheckpointService().getBestTotalTimes(ArenaManager.get(statistic.getId()));
		List<LeaderboardRanking> rankings = new ArrayList<>();
		int skipped = 1;
		int rank = 0;
		long previousTotal = -1;
		for (RecordTotalTime doc : list) {
			long total = doc.getTime().toMillis();

			if (total == 0)
				continue;

			if (total != previousTotal) {
				rank += skipped;
				skipped = 1;
				previousTotal = total;
			}
			else {
				skipped++;
			}

			rankings.add(new LeaderboardRanking(doc.getUuid(), doc.getNickname(), rank, statistic.format(total)));
		}
		return rankings;
	}

	@Override
	public String aggregate(MinigameStatistic statistic, LocalDateTime after, UUID self) {
		if (self == null)
			return "N/A";

		CheckpointService service = new CheckpointService();
		CheckpointUser user = service.get(self);

		Arena arena = ArenaManager.get(statistic.getId());
		RecordTotalTime time = user.getBestTotalTime(arena);
		if (time != null && time.getTime() != null)
			return (String) statistic.format(time.getTime().toMillis());
		else
			return "N/A";
	}

	public static final MinigameStatistic WINS = new MinigameStatistic("wins", "Wins", true);
	public static final MinigameStatistic TIME_PLAYED = new MinigameStatistic("time_played", "Time Played", true) {
		@Override
		public Object format(long score) {
			return Timespan.ofSeconds(score).format();
		}
	};
	public static final MinigameStatistic GAMES_PLAYED = new MinigameStatistic("games_played", "Games Played", true);

}
