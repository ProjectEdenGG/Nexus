package me.pugabyte.nexus.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		YEAR("y", "yr", "year"),
		WEEK("w", null, "week"),
		DAY("d", null, "day"),
		HOUR("h", "hr", "hour"),
		MINUTE("m", "min", "minute"),
		SECOND("s", "sec", "second");

		private final String shortLabel, mediumLabel, longLabel;

		public int of(String input) {
			try {
				double multiplier = Double.parseDouble(input.replaceAll("[^\\d.]+", ""));
				return Time.valueOf(name()).x(multiplier);
			} catch (NumberFormatException ex) {
				throw new InvalidInputException("Invalid " + name().toLowerCase() + ": &e" + input);
			}
		}

		public Pattern getPattern() {
			return Pattern.compile("\\d+(\\.\\d+)?( )?(" + longLabel + "|" + (mediumLabel == null ? "" : mediumLabel + "|") + shortLabel + ")(s)?");
		}

		public static Pattern getAllPattern() {
			StringBuilder regex = new StringBuilder();
			for (TimespanElement element : values())
				regex.append("(").append(element.getPattern().pattern()).append("( )?){0,}");
			return Pattern.compile(regex.toString());
		}
	}

	public enum TimespanFormatType {
		SHORT {
			@Override
			public String get(TimespanElement label, int value) {
				return label.getShortLabel();
			}
		},
		MEDIUM {
			@Override
			public String get(TimespanElement label, int value) {
				return StringUtils.plural(label.getMediumLabel() == null ? label.getLongLabel() : label.getMediumLabel(), value);
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
		@Getter
		private final String rest;

		@Builder
		public Timespan(int seconds, boolean noneDisplay, TimespanFormatType formatType, String rest) {
			this.original = seconds;
			this.seconds = seconds;
			this.noneDisplay = noneDisplay;
			this.formatType = formatType == null ? TimespanFormatType.SHORT : formatType;
			this.rest = rest;
			calculate();
		}

		public static Timespan of(long seconds) {
			return TimespanBuilder.of(seconds).build();
		}

		public static Timespan of(int seconds) {
			return TimespanBuilder.of(seconds).build();
		}

		public static Timespan of(String input) {
			return TimespanBuilder.of(input).build();
		}

		public static Timespan find(String input) {
			return TimespanBuilder.find(input).build();
		}

		public static class TimespanBuilder {

			public static TimespanBuilder of(long seconds) {
				return of(Long.valueOf(seconds).intValue());
			}

			public static TimespanBuilder of(int seconds) {
				return Timespan.builder().seconds(seconds);
			}

			public static TimespanBuilder of(String input) {
				int seconds = 0;
				for (TimespanElement element : TimespanElement.values()) {
					Matcher matcher = element.getPattern().matcher(input);

					while (matcher.find())
						seconds += element.of(matcher.group());
				}
				return of(seconds / Time.SECOND.get());
			}

			public static TimespanBuilder find(String input) {
				Matcher matcher = TimespanElement.getAllPattern().matcher(input);
				while (matcher.find()) {
					String group = matcher.group().trim();
					if (group.length() == 0) continue;
					return TimespanBuilder.of(group).rest(input.replaceFirst(group, "").trim());
				}

				return TimespanBuilder.of(0).rest(input);
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

	@AllArgsConstructor
	public enum Time {
		TICK(1),
		SECOND(TICK.get() * 20),
		MINUTE(SECOND.get() * 60),
		HOUR(MINUTE.get() * 60),
		DAY(HOUR.get() * 24),
		WEEK(DAY.get() * 7),
		MONTH(DAY.get() * 30),
		YEAR(DAY.get() * 365);

		private final int value;

		public int get() {
			return value;
		}

		public int x(int multiplier) {
			return value * multiplier;
		}

		public int x(double multiplier) {
			return (int) (value * multiplier);
		}

	}

	public static class Timer {
		private static final int IGNORE = 2000;

		public Timer(String id, Runnable runnable) {
			long startTime = System.currentTimeMillis();

			runnable.run();

			long duration = System.currentTimeMillis() - startTime;
			if (duration >= 1)
				if (Nexus.isDebug() || duration > IGNORE)
					Nexus.log("[Timer] " + id + " took " + duration + "ms");
		}
	}

}
