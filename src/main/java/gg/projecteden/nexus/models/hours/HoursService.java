package gg.projecteden.nexus.models.hours;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@ObjectClass(Hours.class)
public class HoursService extends MongoPlayerService<Hours> {
	private final static Map<UUID, Hours> cache = new ConcurrentHashMap<>();

	public Map<UUID, Hours> getCache() {
		return cache;
	}

	protected Hours getNoCache(UUID uuid) {
		Hours hours = database.createQuery(getObjectClass()).field(_id).equal(uuid).first();
		if (hours == null) {
			hours = createObject(uuid);
			save(hours);
		}
		return hours;
	}

	@NotNull
	private static MongoCollection<Document> collection() {
		return database.getDatabase().getCollection("hours");
	}

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
	public static class PageResult implements PlayerOwnedObject {
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
		return getPageResults(collection().aggregate(arguments));
	}

	@NotNull
	public List<PageResult> getPageResults(AggregateIterable<Document> aggregate) {
		return new ArrayList<>() {{
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
				Aggregates.project(Projections.computed("times", new BasicDBObject("$objectToArray", "$times"))),
				Aggregates.unwind("$times"),
				Aggregates.match(Filters.regex("times.k", args.getRegex())),
				Aggregates.group("$_id", new BsonField("total", new BasicDBObject("$sum", "$times.v"))),
				Aggregates.sort(Sorts.descending("total"))
		));
	}

	// TODO
	private static final List<UUID> activePlayers = new ArrayList<>();

	public List<UUID> getActivePlayers() {
		if (activePlayers.isEmpty()) {
			List<Bson> arguments = getTopArguments();
			arguments.add(Aggregates.limit(100));

			activePlayers.addAll(
					getPageResults(collection().aggregate(arguments)).stream()
							.map(PageResult::getUuid)
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
			return StringUtils.camelCase(name()).replaceAll(" ", "");
		}

		public static String valuesString() {
			return Arrays.stream(values())
					.map(Enum::name)
					.collect(Collectors.joining(","))
					.toLowerCase();
		}
	}

	@Data
	public static class HoursTopArguments {
		private int year = -1;
		private int month = -1;
		private int day = -1;
		private int page = 1;
		private String input;

		public HoursTopArguments() {
			this("");
		}

		public HoursTopArguments(String input) {
			LocalDate now = LocalDate.now();

			String[] args = input.split(" ");
			String[] split = args[0].split("-");
			this.input = args[0];
			if (Utils.isInt(this.input) && Integer.parseInt(this.input) < 2015) // its page number
				this.input = "";

			switch (args[0]) {
				case "day":
				case "daily":
					day = now.getDayOfMonth();
					month = now.getMonthValue();
					year = now.getYear();
					break;
				case "month":
				case "monthly":
					month = now.getMonthValue();
					year = now.getYear();
					break;
				case "year":
				case "yearly":
					year = now.getYear();
					break;
				default:
					if (split[0].length() > 0) {
						if (Utils.isInt(split[0])) {
							int yearInput = Integer.parseInt(split[0]);
							if (yearInput >= 2015)
								if (yearInput <= 2019)
									throw new InvalidInputException("Years 2015-2019 are not supported");
								else if (yearInput > now.getYear())
									throw new InvalidInputException("Year &e" + yearInput + " &cis in the future");
								else
									year = yearInput;
							else {
								page = yearInput;
								break;
							}

							if (split.length >= 2) {
								if (split[1].length() > 0 && Utils.isInt(split[1])) {
									int monthInput = Integer.parseInt(split[1]);
									if (monthInput >= 1 && monthInput <= 12)
										if (YearMonth.of(year, monthInput).isAfter(YearMonth.now()))
											throw new InvalidInputException("Month &e" + yearInput + "-" + monthInput + " &cis in the future");
										else
											month = monthInput;
									else
										throw new InvalidInputException("Invalid month &e" + monthInput);
								} else
									throw new InvalidInputException("Invalid month &e" + split[1]);

								if (split.length >= 3) {
									if (split[2].length() > 0 && Utils.isInt(split[2])) {
										int dayInput = Integer.parseInt(split[2]);
										if (YearMonth.of(year, month).isValidDay(dayInput))
											if (LocalDate.of(year, month, dayInput).isAfter(now))
												throw new InvalidInputException("Day &e" + year + "-" + month + "-" + dayInput + " &cis in the future");
											else
												day = dayInput;
										else
											throw new InvalidInputException("Invalid day of month &e" + dayInput);
									} else
										throw new InvalidInputException("Invalid day &e" + split[2]);
								}
							}
						} else
							throw new InvalidInputException("Invalid year &e" + split[0]);
					}
			}

			if (args.length >= 2 && Utils.isInt(args[1]))
				page = Integer.parseInt(args[1]);

			if (year == 2020) {
				if (month == -1)
					throw new InvalidInputException("Year 2020 is not supported");
				else if (month <= 5)
					throw new InvalidInputException("Months Jan-May of 2020 are not supported");
			}

			if (page < 1)
				throw new InvalidInputException("Page cannot be less than 1");
		}

		@ToString.Include
		public String getRegex() {
			if (year <= 0)
				return ".*";

			String regex = year + "-";
			if (month > 0) {
				regex += String.format("%02d", month) + "-";
				if (day > 0)
					regex += String.format("%02d", day);
				else
					regex += ".*";
			} else
				regex += ".*";

			return regex;
		}
	}

}
