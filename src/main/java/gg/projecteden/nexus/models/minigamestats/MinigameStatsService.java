package gg.projecteden.nexus.models.minigamestats;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
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
import java.util.Arrays;
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

	public List<LeaderboardRanking> getLeaderboard(MechanicType mechanic, MinigameStatistic statistic, LocalDateTime after, UUID self) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		String afterInstanceString = formatter.format(after == null ? EPOCH : after);

		List<Bson> pipeline = Arrays.asList(
			Aggregates.unwind("$statistics"),
			Aggregates.match(Filters.and(
				Filters.gt("statistics.date", afterInstanceString),
				Filters.eq("statistics.mechanic", mechanic.name())
			)),
			Aggregates.group("$_id", Accumulators.sum("total", "$statistics.stats." + statistic.getId())),
			Aggregates.sort(Sorts.descending("total")),
			Aggregates.limit(10)
		);

		AggregateIterable<Document> top10 = collection().aggregate(pipeline);

		List<LeaderboardRanking> rankings = new ArrayList<>();
		int rank = 1;
		for (Document doc : top10) {
			UUID uuid = UUID.fromString(doc.getString("_id"));
			int total = doc.getInteger("total");
			rankings.add(new LeaderboardRanking(uuid, Nerd.of(uuid).getNickname(), rank++, statistic.format(total)));
		}

		if (self != null) {
			List<Bson> selfScorePipeline = Arrays.asList(
				Aggregates.unwind("$statistics"),
				Aggregates.match(Filters.and(
					Filters.eq("_id", self.toString()),
					Filters.gt("statistics.date", afterInstanceString),
					Filters.eq("statistics.mechanic", mechanic.name())
				)),
				Aggregates.group("$_id", Accumulators.sum("total", "$statistics.stats." + statistic.getId()))
			);

			Document selfScoreDoc = collection().aggregate(selfScorePipeline).first();
			if (selfScoreDoc == null) {
				return rankings;
			}

			int selfScore = selfScoreDoc.getInteger("total");

			List<Bson> selfRankPipeline = Arrays.asList(
				Aggregates.unwind("$statistics"),
				Aggregates.match(Filters.and(
					Filters.gt("statistics.date", afterInstanceString),
					Filters.eq("statistics.mechanic", mechanic.name())
				)),
				Aggregates.group("$_id", Accumulators.sum("total", "$statistics.stats." + statistic.getId())),
				Aggregates.match(Filters.gt("total", selfScore)),
				Aggregates.count("rank")
			);

			Document rankDoc = collection().aggregate(selfRankPipeline).first();
			int selfRank = (rankDoc != null ? rankDoc.getInteger("rank") : 0) + 1;

			rankings.add(new LeaderboardRanking(self, Nerd.of(self).getNickname(), selfRank, statistic.format(selfScore)));
		}

		return rankings;
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
