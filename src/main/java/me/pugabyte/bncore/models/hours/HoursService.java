package me.pugabyte.bncore.models.hours;

import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.BaseService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HoursService extends BaseService {
	@Override
	public Hours get(String uuid) {
		Hours hours = database.where("uuid = ?", uuid).first(Hours.class);
		if (hours.getUuid() == null)
			hours = new Hours(uuid);
		return hours;
	}

	public int total(HoursType type) {
		return database.sql("select sum(" + type.name() + ") from hours").first(Double.class).intValue();
	}

	public List<Hours> getPage(HoursType type, int page) {
		return database.orderBy(type.name() + " desc").limit(10).offset((page - 1) * 10).results(Hours.class);
	}

	public int cleanup() {
		return database
				.table("hours")
				.innerJoin("nerd")
					.on("nerd.uuid = hours.uuid")
				.where("hours.total < (30 * 60)")
				.and("nerd.lastJoin < DATE_ADD(NOW(), INTERVAL -30 DAY)")
				.delete()
				.getRowsAffected();
	}

	public void endOfDay() {
		database.sql("update hours set yesterday = daily, daily = 0").execute();
	}

	public void endOfWeek() {
		database.sql("update hours set lastWeek = weekly, weekly = 0").execute();
	}

	public void endOfMonth() {
		database.sql("update hours set lastMonth = monthly, monthly = 0").execute();
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

		public static String valuesString() {
			return Arrays.stream(values())
					.map(Enum::name)
					.collect(Collectors.joining(","))
					.toLowerCase();
		}
	}

}
