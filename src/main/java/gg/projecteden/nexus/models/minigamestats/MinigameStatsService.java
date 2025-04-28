package gg.projecteden.nexus.models.minigamestats;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.FormulaStatistic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.models.nerd.Nerd;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(MinigameStatsUser.class)
public class MinigameStatsService extends MongoPlayerService<MinigameStatsUser> {
	private final static Map<UUID, MinigameStatsUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, MinigameStatsUser> getCache() {
		return cache;
	}

	@NotNull
	private static MongoCollection<Document> collection() {
		return database.getDatabase().getCollection("minigamer_stats");
	}

	private static final LocalDateTime EPOCH = LocalDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);

	public List<LeaderboardRanking> getLeaderboard(MechanicType mechanic, MinigameStatistic statistic, LocalDateTime after) {
		if (mechanic != null) {
			List<LeaderboardRanking> leaderboardRankings = mechanic.getStatisticsClass().getLeaderboard(statistic, after);
			if (leaderboardRankings != null)
				return leaderboardRankings;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		String afterInstanceString = formatter.format(after == null ? EPOCH : after);

		List<Bson> pipeline = statistic.getPipeline(afterInstanceString, mechanic, null, false);
		AggregateIterable<Document> top10 = collection().aggregate(pipeline);

		List<LeaderboardRanking> rankings = new ArrayList<>();
		int skipped = 1;
		int rank = 0;
		double previousTotal = -1;
		for (Document doc : top10) {
			UUID uuid = UUID.fromString(doc.getString("_id"));
			double total;
			try { total = doc.getInteger("total"); }
			catch (Exception ex) {
				try { total = doc.getDouble("total"); }
				catch (Exception ex2) { return new ArrayList<>(); }
			}

			if (total == 0 && !(statistic instanceof FormulaStatistic))
				continue;

			if (total != previousTotal) {
				rank += skipped;
				skipped = 1;
				previousTotal = total;
			}
			else {
				skipped++;
			}

			rankings.add(new LeaderboardRanking(uuid, Nerd.of(uuid).getNickname(), rank, statistic.format(total)));
		}

		return rankings;
	}

	public String getAggregates(MechanicType mechanic, MinigameStatistic statistic, LocalDateTime after, UUID self) {
		// Allow overriding by MatchStatistics class
		if (mechanic != null) {
			String aggregate = mechanic.getStatisticsClass().aggregate(statistic, after, self);
			if (aggregate != null)
				return aggregate;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		String afterInstanceString = formatter.format(after == null ? EPOCH : after);

		List<Bson> pipeline = statistic.getPipeline(afterInstanceString, mechanic, self, true);

		AggregateIterable<Document> results = collection().aggregate(pipeline);
		double i = 0;
		for (Document doc : results) {
			try { i += doc.getInteger("total"); }
			catch (Exception ex) {
				try { i += doc.getDouble("total"); }
				catch (Exception ex2) { return null; }
			}
		}
		return (String) statistic.format(i);
	}

	@Data
	@AllArgsConstructor
	public static class LeaderboardRanking {
		UUID uuid;
		String name;
		int rank;
		Object score;
	}

}
