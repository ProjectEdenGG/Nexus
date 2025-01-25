package gg.projecteden.nexus.models.statistics;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

	public Map<UUID, StatisticsUser> getCache() {
		return cache;
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
				new StatisticsUserService().getAvailableStats().forEach((group, stats) -> group.availableStats = stats);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@NotNull
	private static MongoCollection<Document> collection() {
		return database.getDatabase().getCollection("statistics");
	}

	public LinkedHashMap<UUID, Long> getLeaderboard(StatisticGroup group, String stat) {
		List<Bson> arguments = Stream.of(
			Aggregates.project(Projections.computed("targetStats", new BasicDBObject("$objectToArray", "$stats.minecraft:" + group.name().toLowerCase()))),
			Aggregates.unwind("$targetStats"),
			isNotNullOrEmpty(stat) ? Aggregates.match(Filters.eq("targetStats.k", "minecraft:" + stat)) : null,
			Aggregates.group("$_id", Accumulators.sum("count", "$targetStats.v")),
			Aggregates.sort(Sorts.descending("count"))
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
			Aggregates.group("$parentKey", Accumulators.addToSet("keys", "$subKeys.k"))
		).toList();

		var results = new HashMap<StatisticGroup, List<String>>();
		collection().aggregate(arguments).forEach(doc -> {
			StatisticGroup group = StatisticGroup.valueOf(doc.getString("_id").replace("minecraft:", "").toUpperCase());
			results.put(group, doc.getList("keys", String.class).stream().map(key -> key.replace("minecraft:", "")).toList());
		});
		return results;
	}

}
