package me.pugabyte.bncore.models.hours;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.Utils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.skip;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Projections.computed;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static me.pugabyte.bncore.utils.StringUtils.camelCase;

@PlayerClass(Hours.class)
public class HoursService extends MongoService {
	private final static Map<UUID, Hours> cache = new HashMap<>();

	public Map<UUID, Hours> getCache() {
		return cache;
	}

	private static final MongoCollection<Document> collection = database.getDatabase().getCollection("hours");

	@Override
	@Deprecated // Use HoursService#update to increment daily counter
	public <T> void save(T object) {
		super.save(object);
	}

	public void update(Hours hours) {
		LocalDate now = LocalDate.now();
		database.update(
				database.createQuery(Hours.class).field(_id).equal(hours.getUuid()),
				database.createUpdateOperations(Hours.class).set("times." + DateTimeFormatter.ISO_DATE.format(now), hours.getDaily(now))
		);
	}

//	public int total(HoursType type) {
//		return database.select("sum(" + type.columnName() + ")").table("hours").first(Double.class).intValue();
//	}

	@Data
	@AllArgsConstructor
	public static class PageResult extends PlayerOwnedObject {
		@Id
		@NonNull
		private UUID uuid;
		private int total;
	}

	public List<PageResult> getPage(int page) {
		List<Bson> arguments = getTopArguments();
		arguments.add(skip((page - 1) * 10));
		arguments.add(limit(10));

		return getPageResults(collection.aggregate(arguments));
	}

	@NotNull
	public List<PageResult> getPageResults(AggregateIterable<Document> aggregate) {
		return new ArrayList<PageResult>() {{
			aggregate.forEach((Consumer<? super Document>) document ->
					add(new PageResult(UUID.fromString((String) document.get("_id")), (int) document.get("total"))));
		}};
	}

	@NotNull
	public List<Bson> getTopArguments() {
		return new ArrayList<>(Arrays.asList(
				project(fields(
						include("_id"),
						computed("times", new BasicDBObject("$objectToArray", "$times"))
				)),
//				project(match(regex("times.k", "2020-05-.*"))),
				project(fields(
						include("_id"),
						computed("total", new BasicDBObject("$sum", "$times.v"))
				)),
				sort(Sorts.descending("total"))
		));
	}

//		return database.createAggregation(Hours2.class)
//				.project(Projection.projection(_id, "_id"), Projection.projection("times", Projection.projection("$objectToArray", "$times")))
//				.project(Projection.projection(_id, "_id"), Projection.projection("total", Projection.projection("$sum", "$times.v")))
//				.sort(Sort.descending("total"))
//				.aggregate(PageResult.class);

	// TODO
	private static final List<OfflinePlayer> activePlayers = new ArrayList<>();

	public List<OfflinePlayer> getActivePlayers() {
		if (activePlayers.isEmpty()) {
			List<Bson> arguments = getTopArguments();
			arguments.add(limit(100));

			activePlayers.addAll(
					getPageResults(collection.aggregate(arguments)).stream()
							.map(pageResult -> Utils.getPlayer(pageResult.getUuid()))
							.collect(Collectors.toList())
			);
		}

		return activePlayers;
	}

	public HoursType getType(String type) {
		if (type == null || type.contains("total")) return HoursType.TOTAL;

		if (type.contains("month"))
			if (type.contains("last"))
				return HoursType.LAST_MONTH;
			else
				return HoursType.MONTHLY;

		if (type.contains("week"))
			if (type.contains("last"))
				return HoursType.LAST_WEEK;
			else
				return HoursType.WEEKLY;

		if (type.contains("day") || type.contains("daily"))
			if (type.contains("yester") || type.contains("last"))
				return HoursType.YESTERDAY;
			else
				return HoursType.DAILY;

		throw new InvalidInputException("Invalid leaderboard type. Options are: " + HoursType.valuesString());
	}

	public enum HoursType {
		TOTAL,
		MONTHLY,
		WEEKLY,
		DAILY,
		LAST_MONTH,
		LAST_WEEK,
		YESTERDAY;

		public String columnName() {
			return camelCase(name()).replaceAll(" ", "");
		}

		public static String valuesString() {
			return Arrays.stream(values())
					.map(Enum::name)
					.collect(Collectors.joining(","))
					.toLowerCase();
		}
	}

}
