package gg.projecteden.nexus.models.statistics;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.models.statistics.StatisticsUserService.MostLeaderboardsResult.LeaderboardStatistic;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;

@ObjectClass(StatisticsUser.class)
public class StatisticsUserService extends MongoPlayerService<StatisticsUser> {
	private final static Map<UUID, StatisticsUser> cache = new ConcurrentHashMap<>();
	private static List<MostLeaderboardsResult> mostLeaderboards = new ArrayList<>();

	public Map<UUID, StatisticsUser> getCache() {
		return cache;
	}

	@Override
	public void clearCache() {
		super.clearCache();
		mostLeaderboards = new ArrayList<>();
	}

	public enum StatisticGroup {
		CUSTOM,
		DROPPED,
		CRAFTED,
		KILLED_BY,
		KILLED,
		BROKEN,
		PICKED_UP,
		USED,
		MINED,
		;

		@Getter
		private List<String> availableStats = new ArrayList<>();

		public static void updateAvailableStats () {
			try {
				new StatisticsUserService().getAvailableStats().forEach((group, stats) -> group.availableStats = stats.stream().filter(stat -> !stat.equals("air")).toList());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public String display() {
			if (this == StatisticGroup.CUSTOM)
				return "misc";

			return name().toLowerCase();
		}
	}

	@NotNull
	private static MongoCollection<Document> collection() {
		return database.getDatabase().getCollection("statistics");
	}

	public LinkedHashMap<UUID, Long> getLeaderboard(StatisticsUserService.StatisticGroup group, String stat) {
		List<Bson> arguments = Stream.of(
			Aggregates.project(Projections.computed("targetStats", new BasicDBObject("$objectToArray", "$stats.minecraft:" + group.name().toLowerCase()))),
			Aggregates.unwind("$targetStats"),
			isNotNullOrEmpty(stat) ? Aggregates.match(Filters.eq("targetStats.k", "minecraft:" + stat)) : null,
			Aggregates.group("$_id", Accumulators.sum("count", "$targetStats.v")),
			Aggregates.sort(Sorts.descending("count", "_id"))
		).filter(Objects::nonNull).toList();

		var results = new LinkedHashMap<UUID, Long>();
		collection().aggregate(arguments).forEach(doc -> results.put(UUID.fromString(doc.getString("_id")), doc.getDouble("count").longValue()));
		return results;
	}

	public Map<StatisticGroup, List<String>> getAvailableStats() {
		List<Bson> arguments = Stream.of(
			Aggregates.project(Projections.fields(Projections.computed("allStats", new Document("$objectToArray", "$stats")))),
			Aggregates.unwind("$allStats"),
			Aggregates.project(Projections.fields(
				Projections.computed("parentKey", "$allStats.k"),
				Projections.computed("subKeys", new Document("$objectToArray", "$allStats.v"))
			)),
			Aggregates.unwind("$subKeys"),
			Aggregates.group("$parentKey",
				Accumulators.addToSet("keys", "$subKeys.k"),
				Accumulators.max("maxValue", "$subKeys.v")
			),
			Aggregates.match(new Document("maxValue", new Document("$gt", 0)))
		).toList();

		var results = new HashMap<StatisticGroup, List<String>>();
		collection().aggregate(arguments).forEach(doc -> {
			StatisticGroup group = StatisticGroup.valueOf(doc.getString("_id").replace("minecraft:", "").toUpperCase());
			results.put(group, doc.getList("keys", String.class).stream().map(key -> key.replace("minecraft:", "")).toList());
		});
		return results;
	}

	@Data
	public static class MostLeaderboardsResult {
		private UUID uuid;
		private List<LeaderboardStatistic> leaderboards;

		public int getCount() {
			return leaderboards.size();
		}

		@Data
		@AllArgsConstructor
		public static class LeaderboardStatistic {
			private String group;
			private String stat;
		}
	}

	public List<MostLeaderboardsResult> getMostLeaderboards() {
		return mostLeaderboards;
	}

	public <T> void calculateMostLeaderboards() {
		List<Bson> arguments = Arrays.asList(
			Aggregates.project(new Document("uuid", "$_id").append("allStats", new Document("$objectToArray", "$stats"))),
			Aggregates.unwind("$allStats"),
			Aggregates.project(new Document("uuid", 1)
				.append("group", "$allStats.k")
				.append("stats", new Document("$objectToArray", "$allStats.v"))
			),
			Aggregates.unwind("$stats"),
			Aggregates.match(Filters.gt("stats.v", 0)),
			Aggregates.group(
				new Document("group", "$group").append("stat", "$stats.k"),
				Accumulators.push("leaders", new Document("uuid", "$uuid").append("value", "$stats.v")),
				Accumulators.max("maxValue", "$stats.v")
			),
			Aggregates.project(new Document("_id", 1)
				.append("leader", new Document("$filter", new Document()
					.append("input", "$leaders")
					.append("as", "leader")
					.append("cond", new Document("$eq", Arrays.asList("$$leader.value", "$maxValue")))
				))
			),
			Aggregates.unwind("$leader"),
			Aggregates.group(
				"$leader.uuid",
				Accumulators.sum("count", 1),
				Accumulators.push("leaderboards", new Document("group", "$_id.group").append("stat", "$_id.stat"))
			),
			Aggregates.sort(Sorts.descending("count", "_id")),
			Aggregates.project(new Document("uuid", "$_id")
				.append("count", 1)
				.append("leaderboards", 1)
			)
		);

		List<MostLeaderboardsResult> list = collection().aggregate(arguments)
			.map(doc -> Utils.getGson().fromJson(doc.toJson().replaceAll("minecraft:", ""), MostLeaderboardsResult.class))
			.into(new ArrayList<>());

		list = list.stream().filter(value -> value.getUuid() != null).toList();

		for (StatisticGroup group : EnumUtils.valuesExcept(StatisticGroup.class, StatisticGroup.CUSTOM)) {
			var leaderIterator = getLeaderboard(group, null).keySet().iterator();
			if (leaderIterator.hasNext()) {
				var leader = leaderIterator.next();
				list.stream()
					.filter(result -> leader.equals(result.getUuid()))
					.findFirst()
					.ifPresent(result -> result.getLeaderboards().addFirst(new LeaderboardStatistic(group.name().toLowerCase(), null)));
			}
		}

		mostLeaderboards = list;
	}

}
