package me.pugabyte.nexus.models.hours;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Sorts;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import me.pugabyte.nexus.features.commands.HoursCommand.HoursTopArguments;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.PlayerUtils;
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

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Aggregates.unwind;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Projections.computed;
import static me.pugabyte.nexus.utils.StringUtils.camelCase;

@PlayerClass(Hours.class)
public class HoursService extends MongoService<Hours> {
	private final static Map<UUID, Hours> cache = new HashMap<>();

	public Map<UUID, Hours> getCache() {
		return cache;
	}

	protected Hours getNoCache(UUID uuid) {
		Hours hours = database.createQuery(getPlayerClass()).field(_id).equal(uuid).first();
		if (hours == null) {
			hours = createPlayerObject(uuid);
			save(hours);
		}
		return hours;
	}

	private static final MongoCollection<Document> collection = database.getDatabase().getCollection("hours");

	@Override
	@Deprecated // Use HoursService#update to increment daily counter
	public void save(Hours object) {
		super.save(object);
	}

	public void update(Hours hours) {
		LocalDate now = LocalDate.now();
		database.update(
				database.createQuery(Hours.class).field(_id).equal(hours.getUuid()),
				database.createUpdateOperations(Hours.class).set("times." + DateTimeFormatter.ISO_DATE.format(now), hours.getDaily(now))
		);
	}

	@Data
	@AllArgsConstructor
	public static class PageResult extends PlayerOwnedObject {
		@Id
		@NonNull
		private UUID uuid;
		private int total;
	}

	public List<PageResult> getPage() {
		return getPage(new HoursTopArguments());
	}

	public List<PageResult> getPage(HoursTopArguments args) {
		List<Bson> arguments = getTopArguments(args);
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
		return getTopArguments(new HoursTopArguments());
	}

	@NotNull
	public List<Bson> getTopArguments(HoursTopArguments args) {
		return new ArrayList<>(Arrays.asList(
				project(computed("times", new BasicDBObject("$objectToArray", "$times"))),
				unwind("$times"),
				match(regex("times.k", args.getRegex())),
				group("$_id", new BsonField("total", new BasicDBObject("$sum", "$times.v"))),
				sort(Sorts.descending("total"))
		));
	}

	// TODO
	private static final List<OfflinePlayer> activePlayers = new ArrayList<>();

	public List<OfflinePlayer> getActivePlayers() {
		if (activePlayers.isEmpty()) {
			List<Bson> arguments = getTopArguments();
			arguments.add(limit(100));

			activePlayers.addAll(
					getPageResults(collection.aggregate(arguments)).stream()
							.map(pageResult -> PlayerUtils.getPlayer(pageResult.getUuid()))
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
