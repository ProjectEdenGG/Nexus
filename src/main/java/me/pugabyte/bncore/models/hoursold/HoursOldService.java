package me.pugabyte.bncore.models.hoursold;

import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.MySQLService;
import me.pugabyte.bncore.utils.Time;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;

public class HoursOldService extends MySQLService {
	private final static Map<String, HoursOld> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	@Override
	public HoursOld get(String uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			HoursOld hours = database.where("uuid = ?", uuid).first(HoursOld.class);
			if (hours.getUuid() == null)
				hours = new HoursOld(uuid);
			return hours;
		});

		return cache.get(uuid);
	}

	public int total(HoursType type) {
		return database.select("sum(" + type.columnName() + ")").table("hours").first(Double.class).intValue();
	}

	public List<HoursOld> getPage(HoursType type, int page) {
		return database.orderBy(type.columnName().replaceAll("_", "") + " desc").limit(10).offset((page - 1) * 10).results(HoursOld.class);
	}

	public List<HoursOld> getActivePlayers() {
		return database.where("total > ?", Time.DAY.x(10) / 20).results(HoursOld.class);
	}

	public List<HoursOld> getAll() {
		return database.results(HoursOld.class);
	}

	public int cleanup() {
		clearCache();
		return database
				.table("hours")
				.innerJoin("nerd")
					.on("nerd.uuid = hours.uuid")
				.where("hours.total < (30 * 60)")
				.and("nerd.lastJoin < DATE_ADD(NOW(), INTERVAL -60 DAY)")
				.delete()
				.getRowsAffected();
	}

	public void endOfDay() {
		clearCache();
		database.sql("update hours set yesterday = daily, daily = 0").execute();
		clearCache();
	}

	public void endOfWeek() {
		clearCache();
		database.sql("update hours set lastWeek = weekly, weekly = 0").execute();
		clearCache();
	}

	public void endOfMonth() {
		clearCache();
		database.sql("update hours set lastMonth = monthly, monthly = 0").execute();
		clearCache();
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
