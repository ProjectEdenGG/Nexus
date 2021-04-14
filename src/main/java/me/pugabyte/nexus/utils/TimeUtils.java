package me.pugabyte.nexus.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class TimeUtils {

	public static String longDateTimeFormat(LocalDateTime dateTime) {
		return longDateFormat(dateTime.toLocalDate()) + " " + longTimeFormat(dateTime);
	}

	public static String shortDateTimeFormat(LocalDateTime dateTime) {
		return shortDateFormat(dateTime.toLocalDate()) + " " + shortTimeFormat(dateTime);
	}

	public static String shortishDateTimeFormat(LocalDateTime dateTime) {
		return shortishDateFormat(dateTime.toLocalDate()) + " " + shortishTimeFormat(dateTime);
	}

	public static String longDateFormat(LocalDate date) {
		return StringUtils.camelCase(date.getMonth().name()) + " " + StringUtils.getNumberWithSuffix(date.getDayOfMonth()) + ", " + date.getYear();
	}

	public static String shortDateFormat(LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern("M/d/yy"));
	}

	public static String shortishDateFormat(LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern("MM/dd/yy"));
	}

	public static String dateFormat(LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
	}

	public static String longTimeFormat(LocalDateTime time) {
		return time.format(DateTimeFormatter.ofPattern("h:mm:ss a"));
	}

	public static String shortTimeFormat(LocalDateTime time) {
		return time.format(DateTimeFormatter.ofPattern("h:mm a"));
	}

	public static String shortishTimeFormat(LocalDateTime time) {
		return time.format(DateTimeFormatter.ofPattern("hh:mm a"));
	}

	public static LocalDate parseShortDate(String input) {
		return LocalDate.from(DateTimeFormatter.ofPattern("M/d/yyyy").parse(input));
	}

	public static LocalDate parseDate(String input) {
		try {
			return parseShortDate(input);
		} catch (DateTimeParseException ignore) {
		}
		try {
			return parseDate(input);
		} catch (DateTimeParseException ignore) {
		}
		throw new InvalidInputException("Could not parse date, correct format is MM/DD/YYYY");
	}

	public static LocalDateTime parseDateTime(String input) {
		try {
			return LocalDateTime.parse(input);
		} catch (DateTimeParseException ignore) {
		}
		throw new InvalidInputException("Could not parse date, correct format is YYYY-MM-DDTHH:MM:SS");
	}

	public static String timespanDiff(LocalDate from) {
		return timespanDiff(from.atStartOfDay());
	}

	public static String timespanDiff(LocalDateTime from) {
		LocalDateTime now = LocalDateTime.now();
		if (from.isBefore(now))
			return timespanDiff(from, now);
		else
			return timespanDiff(now, from);
	}

	public static String timespanDiff(LocalDateTime from, LocalDateTime to) {
		return Timespan.of(Long.valueOf(from.until(to, ChronoUnit.SECONDS)).intValue()).format();
	}

	@Getter
	@AllArgsConstructor
	public enum TimespanElement {
		YEAR("y", "year"),
		MONTH("mo", "month"),
		WEEK("w", "week"),
		DAY("d", "day"),
		HOUR("h", "hour"),
		MINUTE("m", "minute"),
		SECOND("s", "second"),
		TICK("t", "tick");

		private final String shortLabel, longLabel;
	}

	public enum TimespanFormatType {
		SHORT {
			@Override
			public String get(TimespanElement label, int value) {
				return label.getShortLabel();
			}
		},
		LONG {
			@Override
			public String get(TimespanElement label, int value) {
				return StringUtils.plural(label.getLongLabel(), value);
			}
		};

		abstract String get(TimespanElement label, int value);
	}

	public static class Timespan {
		private final int original;
		private final boolean noneDisplay;
		private final TimespanFormatType formatType;
		private int years, days, hours, minutes, seconds;

		@lombok.Builder(buildMethodName = "_build")
		public Timespan(int seconds, boolean noneDisplay, TimespanFormatType formatType) {
			this.original = seconds;
			this.seconds = seconds;
			this.noneDisplay = noneDisplay;
			this.formatType = formatType == null ? TimespanFormatType.SHORT : formatType;
			calculate();
		}

		public static TimespanBuilder of(long seconds) {
			return of(Long.valueOf(seconds).intValue());
		}

		public static TimespanBuilder of(int seconds) {
			return Timespan.builder().seconds(seconds);
		}

		public static class TimespanBuilder {

			public String format() {
				return _build().format();
			}

			@Deprecated
			public Timespan build() {
				throw new UnsupportedOperationException("Use format()");
			}

		}

		private void calculate() {
			if (seconds == 0) return;

			years = seconds / 60 / 60 / 24 / 365;
			seconds -= years * 60 * 60 * 24 * 365;
			days = seconds / 60 / 60 / 24;
			seconds -= days * 60 * 60 * 24;
			hours = seconds / 60 / 60;
			seconds -= hours * 60 * 60;
			minutes = seconds / 60;
			seconds -= minutes * 60;
		}

		public String format() {
			if (original == 0 && noneDisplay)
				return "None";

			String result = "";
			if (years > 0)
				result += years + formatType.get(TimespanElement.YEAR, years) + " ";
			if (days > 0)
				result += days + formatType.get(TimespanElement.DAY, days) + " ";
			if (hours > 0)
				result += hours + formatType.get(TimespanElement.HOUR, hours) + " ";
			if (minutes > 0)
				result += minutes + formatType.get(TimespanElement.MINUTE, minutes) + " ";
			if (years == 0 && days == 0 && hours == 0 && minutes > 0 && seconds > 0)
				result += seconds + formatType.get(TimespanElement.SECOND, seconds);

			if (result.length() == 0)
				result = original + formatType.get(TimespanElement.SECOND, seconds);

			return result.trim();
		}

	}

}
