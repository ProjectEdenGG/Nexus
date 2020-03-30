package me.pugabyte.bncore.utils;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {
	public static String getPrefix(String prefix) {
		return colorize("&8&l[&e" + prefix + "&8&l]&3 ");
	}

	public static String colorize(String string) {
		if (string == null)
			return null;
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static String decolorize(String string) {
		return string.replaceAll(getColorChar(), "&");
	}

	public static String stripColor(String string) {
		return ChatColor.stripColor(colorize(string));
	}

	public static String getColorChar() {
		return "§";
	}

	public static String loreize(String string) {
		int i = 0, lineLength = 0;
		boolean watchForNewLine = false, watchForColor = false;
		string = colorize(string);

		for (String character : string.split("")) {
			if (character.contains("\n")) {
				lineLength = 0;
				continue;
			}

			if (watchForNewLine) {
				if ("|".equalsIgnoreCase(character))
					lineLength = 0;
				watchForNewLine = false;
			} else if ("|".equalsIgnoreCase(character))
				watchForNewLine = true;

			if (watchForColor) {
				if (character.matches("[A-Fa-fK-Ok-oRr0-9]"))
					lineLength -= 2;
				watchForColor = false;
			} else if ("&".equalsIgnoreCase(character))
				watchForColor = true;

			++lineLength;

			if (lineLength > 28)
				if (" ".equalsIgnoreCase(character)) {
					String before = left(string, i);
					String excess = right(string, string.length() - i);
					if (excess.length() > 5) {
						excess = excess.trim();
						boolean doSplit = true;
						if (excess.contains("||") && excess.indexOf("||") <= 5)
							doSplit = false;
						if (excess.contains(" ") && excess.indexOf(" ") <= 5)
							doSplit = false;
						if (lineLength >= 38)
							doSplit = true;

						if (doSplit) {
							string = before + "||" + getLastColor(before) + excess.trim();
							lineLength = 0;
							i += 4;
						}
					}
				}

			++i;
		}

		return string;
	}

	public static List<String> splitLore(String lore) {
		return new ArrayList<>(Arrays.asList(lore.split("\\|\\|")));
	}

	public static String getLastColor(String text) {
		String reversed = new StringBuilder(colorize(text)).reverse().toString();
		StringBuilder result = new StringBuilder();
		String lastChar = null;

		for (String character : reversed.split("")) {
			if (character.equals(getColorChar()) && lastChar != null) {
				ChatColor color = ChatColor.getByChar(lastChar);

				if (color != null) {
					if (color.isFormat()) {
						result.insert(0, color.toString());
						continue;
					} else {
						if (color == ChatColor.RESET)
							color = ChatColor.WHITE;
						result.insert(0, color);
						break;
					}
				}
			}

			lastChar = character;
		}

		return result.toString();
	}

	public static String trimFirst(String string) {
		return right(string, string.length() - 1);
	}

	public static String right(String string, int number) {
		return string.substring(Math.max(string.length() - number, 0));
	}

	public static String left(String string, int number) {
		return string.substring(0, Math.min(number, string.length()));
	}

	public static String camelCase(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		return Arrays.stream(text.replaceAll("_", " ").split(" "))
				.map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
				.collect(Collectors.joining(" "));
	}

	public static String camelCaseWithUnderscores(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		return Arrays.stream(text.split("_"))
				.map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
				.collect(Collectors.joining("_"));
	}

	public static String listFirst(String string, String delimiter) {
		return string.split(delimiter)[0];
	}

	public static String listLast(String string, String delimiter) {
		return string.substring(string.lastIndexOf(delimiter) + 1);
	}

	public static String listGetAt(String string, int index, String delimiter) {
		String[] split = string.split(delimiter);
		return split[index - 1];
	}

	public static String replaceLast(String text, String regex, String replacement) {
		return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
	}

	public static String uuidFormat(String uuid) {
		uuid = uuidUnformat(uuid);
		String formatted = "";
		formatted += uuid.substring(0, 8) + "-";
		formatted += uuid.substring(8, 12) + "-";
		formatted += uuid.substring(12, 16) + "-";
		formatted += uuid.substring(16, 20) + "-";
		formatted += uuid.substring(20, 32);
		return formatted;
	}

	private static String uuidUnformat(String uuid) {
		return uuid.replaceAll("-", "");
	}

	public enum ProgressBarStyle {
		NONE,
		COUNT,
		PERCENT
	}

	public static String progressBar(int progress, int goal) {
		return progressBar(progress, goal, ProgressBarStyle.NONE, 25);
	}

	public static String progressBar(int progress, int goal, ProgressBarStyle style) {
		return progressBar(progress, goal, style, 25);
	}

	public static String progressBar(int progress, int goal, ProgressBarStyle style, int length) {
		double percent = Math.min(progress / goal, 1);
		ChatColor color = ChatColor.RED;
		if (percent == 1)
			color = ChatColor.GREEN;
		else if (percent >= 2/3)
			color = ChatColor.YELLOW;
		else if (percent >= 1/3)
			color = ChatColor.GOLD;

		int n = (int) Math.floor(percent * length);

		String bar = String.join("", Collections.nCopies(length, "|"));
		String first = left(bar, n);
		String last = right(bar, length - n);
		String result = color + first + "&8" + last;

		// TODO: Style
		if (style == ProgressBarStyle.COUNT)
			result += " &f" + progress + "/" + goal;
		if (style == ProgressBarStyle.PERCENT)
			result += " &f" + Math.floor(percent * 100);

		return result;
	}

	public static String timespanDiff(LocalDateTime from) {
		return timespanDiff(from, LocalDateTime.now());
	}

	public static String timespanDiff(LocalDateTime from, LocalDateTime to) {
		return timespanFormat(from.until(to, ChronoUnit.SECONDS));
	}

	public static String timespanFormat(long seconds) {
		return timespanFormat(Long.valueOf(seconds).intValue());
	}

	public static String timespanFormat(int seconds) {
		return timespanFormat(seconds, null);
	}

	public static String timespanFormat(int seconds, String noneDisplay) {
		if (seconds == 0 && !Strings.isNullOrEmpty(noneDisplay)) return noneDisplay;

		int original = seconds;
		int years = seconds / 60 / 60 / 24 / 365;
		seconds -= years * 60 * 60 * 24 * 365;
		int days = seconds / 60 / 60 / 24;
		seconds -= days * 60 * 60 * 24;
		int hours = seconds / 60 / 60;
		seconds -= hours * 60 * 60;
		int minutes = seconds / 60;
		seconds -= minutes * 60;

		String result = "";
		if (years > 0)
			result += years + "y ";
		if (days > 0)
			result += days + "d ";
		if (hours > 0)
			result += hours + "h ";
		if (minutes > 0)
			result += minutes + "m ";
		if (years == 0 && days == 0 && hours == 0 && minutes > 0 && seconds > 0)
			result += seconds + "s ";

		if (result.length() > 0)
			return result.trim();
		else
			return original + "s";
	}

	public static String longDateTimeFormat(LocalDateTime dateTime) {
		return longDateFormat(dateTime.toLocalDate()) + " " + longTimeFormat(dateTime);
	}

	public static String shortDateTimeFormat(LocalDateTime dateTime) {
		return shortDateFormat(dateTime.toLocalDate()) + " " + shortTimeFormat(dateTime);
	}

	public static String longDateFormat(LocalDate date) {
		return camelCase(date.getMonth().name()) + " " + getNumberSuffix(date.getDayOfMonth()) + ", " + date.getYear();
	}

	public static String shortDateFormat(LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern("M/d/YY"));
	}

	public static String longTimeFormat(LocalDateTime time) {
		return time.format(DateTimeFormatter.ofPattern("h:mm:ss a"));
	}

	public static String shortTimeFormat(LocalDateTime time) {
		return time.format(DateTimeFormatter.ofPattern("h:mm a"));
	}

	public static LocalDate parseShortDate(String input) {
		return LocalDate.from(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).parse(input));
	}

	public static String getNumberSuffix(int number) {
		String text = String.valueOf(number);
		if (text.endsWith("1"))
			if (text.endsWith("11"))
				return number + "th";
			else
				return number + "st";
		else if (text.endsWith("2"))
			if (text.endsWith("12"))
				return number + "th";
			else
				return number + "nd";
		else if (text.endsWith("3"))
			if (text.endsWith("13"))
				return number + "th";
			else
				return number + "rd";
		else
			return number + "th";
	}

	public static String getLocationString(Location loc) {
		return "&3World: &e" + loc.getWorld().getName() + " &3x: &e" + loc.getX() + " &3y: &e" +
				loc.getY() + " &3z: &e" + loc.getZ();
	}

	public static void sendJsonLocation(String message, Location location, Player player) {
		int x = (int) location.getX();
		int y = (int) location.getY();
		int z = (int) location.getZ();
		double yaw = location.getYaw();
		double pitch = location.getPitch();
		String world = location.getWorld().getName();

		new JsonBuilder().next(message).command("/tppos " + x + " " + y + " " + z + " " + yaw + " " + pitch + " " + world).send(player);
	}

}
