package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.Nexus;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;

public class DateUtils {
	private static final int YEAR = LocalDateTime.now().getYear();
	public static final int MIN_HOUR = 0;
	public static final int MIN_MINUTE = 0;
	public static final int MAX_HOUR = 23;
	public static final int MAX_MINUTE = 59;

	// Start Time
	public static LocalDateTime getStart(Month month) {
		return getStart(month, 1, MIN_HOUR, MIN_MINUTE);
	}

	public static LocalDateTime getStart(Month month, int day) {
		return getStart(month, day, MIN_HOUR, MIN_MINUTE);
	}

	public static LocalDateTime getStart(Month month, int day, int hour, int minute) {
		return getDateTime(month, day, hour, minute);
	}

	// End Time
	public static LocalDateTime getEnd(Month month) {
		return getEnd(month, getLastDay(YearMonth.of(YEAR, month.getValue())), MAX_HOUR, MAX_MINUTE);
	}

	public static LocalDateTime getEnd(Month month, int day) {
		return getEnd(month, day, MAX_HOUR, MAX_MINUTE);
	}

	public static LocalDateTime getEnd(Month month, int day, int hour, int minute) {
		return getDateTime(month, day, hour, minute);
	}

	//

	public static LocalDateTime getDateTime(Month month) {
		return getDateTime(month, 1, MIN_HOUR, MIN_MINUTE);
	}

	public static LocalDateTime getDateTime(Month month, int day) {
		return getDateTime(month, day, MIN_HOUR, MIN_MINUTE);
	}

	public static LocalDateTime getDateTime(Month month, int day, int hour, int minute) {
		day = MathUtils.clamp(day, 1, getLastDay(YearMonth.of(YEAR, month.getValue())));
		return LocalDateTime.of(YEAR, month, day, hour, minute);
	}

	//

	public static int getLastDay(YearMonth month) {
		return month.atEndOfMonth().getDayOfMonth();
	}

	public static boolean isWithin(LocalDateTime value, LocalDateTime start, LocalDateTime end) {
		if (start.isAfter(end)) {
			Nexus.warn("DateUtils#isWithin: Start Time (" + start.toString() + ") is after End Time (" + end + ")");
			Thread.dumpStack();
			return false;
		}

		if (end.isBefore(start)) {
			Nexus.warn("DateUtils#isWithin: End Time (" + end + ") is before Start Time (" + start + ")");
			Thread.dumpStack();
			return false;
		}

		boolean isEqualOrAfter = value.equals(start) || value.isAfter(start);
		boolean isEqualOrBefore = value.equals(end) || value.isBefore(end);

		return isEqualOrAfter && isEqualOrBefore;
	}
}
