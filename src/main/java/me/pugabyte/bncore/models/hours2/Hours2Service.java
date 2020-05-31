package me.pugabyte.bncore.models.hours2;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Sorts;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.NerdService;
import org.bson.Document;
import org.bukkit.OfflinePlayer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.skip;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Projections.computed;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static me.pugabyte.bncore.utils.StringUtils.camelCase;

/*
/hours top month:march20
/hours top month:august19 30
/hours top year:2020
/hours top monthly
/hours top daily 5
/hours top
 */

@PlayerClass(Hours2.class)
public class Hours2Service extends MongoService {
	private final static Map<UUID, Hours2> cache = new HashMap<>();

	public Map<UUID, Hours2> getCache() {
		return cache;
	}

	@Override
	@Deprecated // Use HoursService#update to increment daily counter
	public <T> void save(T object) {
		super.save(object);
	}

	public void update(Hours2 hours2) {
		database.update(
				database.createQuery(Hours2.class).field(_id).equal(hours2.getUuid()),
				database.createUpdateOperations(Hours2.class).set("times." + DateTimeFormatter.ISO_DATE.format(LocalDate.now()), hours2.getDaily())
		);
	}

	public void migrate() {
		int count = 0;
		for (Hours hours : new HoursService().getAll()) {
			OfflinePlayer player = hours.getPlayer();
			Nerd nerd = new NerdService().get(player);
			Hours2 hours2 = new Hours2(player.getUniqueId());

			if (nerd.getLastQuit() == null || nerd.getFirstJoin().isAfter(nerd.getLastQuit())) continue;

			LocalDate start = nerd.getFirstJoin().toLocalDate().withDayOfMonth(1);
			LocalDate end = nerd.getLastQuit().toLocalDate().withDayOfMonth(1);

			int months = (int) ChronoUnit.MONTHS.between(start, end);
			int spread = (hours.getTotal() / Math.max(months, 1)) / 5 * 5;

			if (start.equals(end))
				hours2.getTimes().put(start, spread);
			else
				while (start.isBefore(end)) {
					hours2.getTimes().put(start, spread);
					start = start.plusMonths(1);
				}

			cache.put(player.getUniqueId(), hours2);
			save(hours2);

			++count;
			if (count % 100 == 0)
				BNCore.log("Migrated " + count + " records");
		}
	}

//	public int total(HoursType type) {
//		return database.select("sum(" + type.columnName() + ")").table("hours").first(Double.class).intValue();
//	}

	/*

		db.hours.aggregate([
			{ $project : { _id : "$_id", times : { $objectToArray: "$times" } } },
			{ $project : { _id : "$_id", total : { $sum : "$times.v" } } },
			{ $sort : { 'total': -1 } }
		]);

	 */

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class PageResult extends PlayerOwnedObject {
		@Id
		@NonNull
		private UUID uuid;
		private int total;
	}

	public List<PageResult> getPage(int page) {
		AggregateIterable<Document> aggregate = database.getDatabase().getCollection("hours").aggregate(Arrays.asList(
				project(fields(
						include("_id"),
						computed("times", new BasicDBObject("$objectToArray", "$times"))
				)),
				project(match(regex("times.k", "2020-05-.*"))),
				project(fields(
						include("_id"),
						computed("total", new BasicDBObject("$sum", "$times.v"))
				)),
				sort(Sorts.descending("total")),
				skip((page - 1) * 10),
				limit(10)
		));

		return new ArrayList<PageResult>() {{
			aggregate.forEach((Consumer<? super Document>) document ->
					add(new PageResult(UUID.fromString((String) document.get("_id")), (int) document.get("total"))));
		}};


//		return database.createAggregation(Hours2.class)
//				.project(Projection.projection(_id, "_id"), Projection.projection("times", Projection.projection("$objectToArray", "$times")))
//				.project(Projection.projection(_id, "_id"), Projection.projection("total", Projection.projection("$sum", "$times.v")))
//				.sort(Sort.descending("total"))
//				.aggregate(PageResult.class);
	}

	// TODO
//	public List<Hours2> getActivePlayers() {
//		return database.where("total > ?", Time.DAY.x(10) / 20).results(Hours2.class);
//	}

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
