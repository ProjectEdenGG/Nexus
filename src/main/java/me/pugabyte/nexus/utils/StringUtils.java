package me.pugabyte.nexus.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import joptsimple.internal.Strings;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import net.md_5.bungee.api.ChatColor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtils {
	@Getter
	private static final String colorChar = "§";
	@Getter
	private static final String altColorChar = "&";
	@Getter
	private static final String colorCharsRegex = "[" + colorChar + altColorChar + "]";
	@Getter
	private static final Pattern colorPattern = Pattern.compile(colorCharsRegex + "[0-9a-fA-F]");
	@Getter
	private static final Pattern formatPattern = Pattern.compile(colorCharsRegex + "[k-orK-OR]");
	@Getter
	private static final Pattern hexPattern = Pattern.compile(colorCharsRegex + "#[a-fA-F0-9]{6}");
	@Getter
	private static final Pattern hexColorizedPattern = Pattern.compile(colorCharsRegex + "x(" + colorCharsRegex + "[a-fA-F0-9]){6}");
	@Getter
	private static final Pattern colorGroupPattern = Pattern.compile("(" + colorPattern + "|(" + hexPattern + "|" + hexColorizedPattern + "))((" + formatPattern + ")+)?");
	@Getter
	public static final String CHECK = "&a✔";
	@Getter
	public static final String X = "&c✗";

	public static String getPrefix(Class<?> clazz) {
		return getPrefix(clazz.getSimpleName());
	}

	public static String getPrefix(String prefix) {
		return colorize("&8&l[&e" + prefix + "&8&l]&3 ");
	}

	public static String getDiscordPrefix(String prefix) {
		return "**[" + prefix + "]** ";
	}

	public static String colorize(String input) {
		if (input == null)
			return null;

		while (true) {
			Matcher matcher = hexPattern.matcher(input);
			if (!matcher.find()) break;

			String color = matcher.group();
			input = input.replace(color, ChatColor.of(color.replaceFirst(colorCharsRegex, "")).toString());
		}

		return ChatColor.translateAlternateColorCodes(altColorChar.charAt(0), input);
	}

	// Replaces § with & and unformats hex, does NOT strip colors
	public static String decolorize(String input) {
		if (input == null)
			return null;

		input = colorize(input);

		while (true) {
			Matcher matcher = hexColorizedPattern.matcher(input);
			if (!matcher.find()) break;

			String color = matcher.group();
			input = input.replace(color, color.replace(colorChar + "x", "&#").replaceAll(colorChar, ""));
		}

		return input.replaceAll(colorChar, altColorChar);
	}

	public static String toHex(ChatColor color) {
		return "#" + Integer.toHexString(color.getColor().getRGB()).substring(2);
	}

	public static String stripColor(String input) {
		return ChatColor.stripColor(colorize(input));
	}

	public static String stripFormat(String input) {
		return formatPattern.matcher(colorize(input)).replaceAll("");
	}

	public static int countUpperCase(String s) {
		return (int) s.codePoints().filter(c-> c >= 'A' && c <= 'Z').count();
	}

	public static int countLowerCase(String s) {
		return (int) s.codePoints().filter(c-> c >= 'a' && c <= 'z').count();
	}

	// TODO This will break with hex
	// TODO replace with https://canary.discord.com/channels/132680070480396288/421474915930079232/827394245522096158
	// 		- needs to strip color when measuring max length and carry colors over to next line
	public static String loreize(String string) {
		if (string == null) return null;

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
		Matcher matcher = colorGroupPattern.matcher(text);
		String last = "";
		while (matcher.find())
			last = matcher.group();
		return last.toLowerCase();
	}

	public static String plural(String label, Number number) {
		return label + (number.doubleValue() == 1 ? "" : "s");
	}

	public static String trimFirst(String string) {
		return string.substring(1);
	}

	public static String right(String string, int number) {
		return string.substring(Math.max(string.length() - number, 0));
	}

	public static String left(String string, int number) {
		return string.substring(0, Math.min(number, string.length()));
	}

	public static String camelCase(Enum<?> _enum) {
		if (_enum == null) return null;
		return camelCase(_enum.name());
	}

	public static String camelCase(String text) {
		if (Strings.isNullOrEmpty(text))
			return text;

		return Arrays.stream(text.replaceAll("_", " ").split(" "))
				.map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
				.collect(Collectors.joining(" "));
	}

	public static String camelCaseWithUnderscores(String text) {
		if (Strings.isNullOrEmpty(text)) {
			return text;
		}

		return Arrays.stream(text.split("_"))
				.map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
				.collect(Collectors.joining("_"));
	}

	public static String asOxfordList(List<String> items, String separator) {
		if (!separator.contains(", "))
			throw new InvalidInputException("Separator must contain ', '");

		String message = String.join(separator, items);
		int commaIndex = message.lastIndexOf(", ");
		message = new StringBuilder(message).replace(commaIndex, commaIndex + 2, (items.size() > 2 ? "," : "") + " and ").toString();
		return message;
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

	public static final String UUID_REGEX = "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";

	public static boolean isUuid(String uuid) {
		return uuid.matches(UUID_REGEX);
	}

	public static boolean isV4Uuid(UUID uuid) {
		return isV4Uuid(uuid.toString());
	}

	public static boolean isV4Uuid(String uuid) {
		return uuid.charAt(14) == '4';
	}

	public static boolean isValidJson(String json) {
		try {
			new JSONObject(json);
		} catch (JSONException ex) {
			try {
				new JSONArray(json);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}

	public static String toPrettyString(Object object) {
		try {
			return getPrettyPrinter().toJson(object);
		} catch (Exception | StackOverflowError ignored) {
			return object.toString();
		}
	}

	public static Gson getPrettyPrinter() {
		return new GsonBuilder().setPrettyPrinting().create();
	}

	public static String pretty(ItemStack item) {
		return item.getAmount() + " " + camelCase(item.getType().name());
	}

	private static final NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();

	public static String pretty(Number number) {
		String format = trimFirst(moneyFormat.format(number));
		if (format.endsWith(".00"))
			format = left(format, format.length() - 3);

		return format;
	}

	public static String prettyMoney(Number number) {
		if (number == null)
			return null;
		return "$" + pretty(number);
	}

	public static String stripTrailingZeros(String number) {
		return number.contains(".") ? number.replaceAll("0*$", "").replaceAll("\\.$", "") : number;
	}

	// Attempt to strip symbols and support euro formatting
	public static String asParsableDecimal(String value) {
		if (value == null)
			return "0";

		value = value.replace("$", "");
		if (value.contains(",") && value.contains("."))
			if (value.indexOf(",") < value.indexOf("."))
				value = value.replaceAll(",", "");
			else {
				value = value.replaceAll("\\.", "");
				value = value.replaceAll(",", ".");
			}
		else if (value.contains(",") && value.indexOf(",") == value.lastIndexOf(","))
			if (value.indexOf(",") == value.length() - 3)
				value = value.replace(",", ".");
			else
				value = value.replace(",", "");
		return value;
	}

	public static String ellipsis(String text, int length) {
		if (text.length() > length)
			return text.substring(0, length) + "...";
		else
			return text;
	}

	public static String bool(boolean b) {
		if (b)
			return "&atrue";
		else
			return "&cfalse";
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
		double percent = Math.min((double) progress / goal, 1);
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

	private static final String[] compassParts = {"[S]","SW","[W]","NW","[N]","NE","[E]","SE"};

	public static String compass(Player player) {
		return compass(player, 8);
	}

	public static String compass(Player player, int extra) {
		return compass(player, extra, 4);
	}

	public static String compass(Player player, int extra, int separators) {
		String compass = "";
		for (String compassPart : compassParts)
			compass += compassPart + " " + String.join("", Collections.nCopies(separators, "-")) + " ";

		float yaw = Location.normalizeYaw(player.getLocation().getYaw());
		if (yaw < 0) yaw = 360 + yaw;

		int center = (int) Math.round(yaw / (360D / compass.length())) + 1;

		String instance;
		if (center - extra < 0) {
			center += compass.length();
			instance = (compass + compass).substring(center - extra, center + extra + 1);
		} else if (center + extra + 1 > compass.length())
			instance = (compass + compass).substring(center - extra, center + extra + 1);
		else
			instance = compass.substring(center - extra, center + extra + 1);

		instance = instance.replaceAll("\\[", "&2[&f");
		instance = instance.replaceAll("]", "&2]&f");
		return colorize(instance);
	}

	public static String distanceMetricFormat(int cm) {
		int original = cm;
		int km = cm / 1000 / 100;
		cm -= km * 1000 * 100;
		int meters = cm / 100;
		cm -= meters * 100;

		String result = "";
		if (km > 0)
			result += km + "km ";
		if (meters > 0)
			result += meters + "m ";

		if (result.length() > 0)
			return result.trim();
		else
			return original + "cm";
	}

	public static String getNumberWithSuffix(int number) {
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

	@Getter
	private static final DecimalFormat df = new DecimalFormat("#.00");

	@Getter
	private static final DecimalFormat nf = new DecimalFormat("#");

	public static DecimalFormat getFormatter(Class<?> type) {
		if (Integer.class == type || Integer.TYPE == type) return nf;
		if (Double.class == type || Double.TYPE == type) return df;
		if (Float.class == type || Float.TYPE == type) return df;
		if (Short.class == type || Short.TYPE == type) return nf;
		if (Long.class == type || Long.TYPE == type) return nf;
		if (Byte.class == type || Byte.TYPE == type) return nf;
		if (BigDecimal.class == type) return df;
		throw new InvalidInputException("No formatter found for class " + type.getSimpleName());
	}

	public static String getLocationString(Location loc) {
		return "&3World: &e" + loc.getWorld().getName() + " &3x: &e" + df.format(loc.getX()) + " &3y: &e" +
				df.format(loc.getY()) + " &3z: &e" +  df.format(loc.getZ());
	}

	public static String getShortLocationString(Location loc) {
		return (int) loc.getX() + " " + (int) loc.getY() + " " +  (int) loc.getZ() + " " + loc.getWorld().getName();
	}

	public static String getShorterLocationString(Location loc) {
		return (int) loc.getX() + " " + (int) loc.getY() + " " +  (int) loc.getZ();
	}

	public static void sendJsonLocation(String message, Location location, Player player) {
		new JsonBuilder().next(message).command(getTeleportCommand(location)).send(player);
	}

	public static String getTeleportCommand(Location location) {
		return "/tppos " + (int) location.getX() + " " + (int) location.getY() + " " + (int) location.getZ() + " " + location.getYaw() + " " + location.getPitch() + " " + location.getWorld().getName();
	}

	private static final String HASTEBIN = "https://paste.bnn.gg/";

	@Data
	private static class PasteResult {
		private String key;
	}

	@SneakyThrows
	public static String paste(String content) {
		Request request = new Request.Builder().url(HASTEBIN + "documents").post(RequestBody.create(MediaType.get("text/plain"), content)).build();
		try (Response response = new OkHttpClient().newCall(request).execute()) {
			PasteResult result = new Gson().fromJson(response.body().string(), PasteResult.class);
			return HASTEBIN + result.getKey();
		}
	}

	@NonNull
	public static String getPaste(String code) throws InvalidInputException {
		try {
			Request request = new Request.Builder().url(HASTEBIN + "raw/" + code).get().build();
			try (Response response = new OkHttpClient().newCall(request).execute()) {
				return response.body().string();
			}
		} catch (Exception ex) {
			throw new InvalidInputException("An error occurred while retrieving the paste data: " + ex.getMessage());
		}
	}

	@RequiredArgsConstructor
	public static class Gradient {
		@NonNull
		private final ChatColor color1, color2;

		public static Gradient of(ChatColor color1, ChatColor color2) {
			return new Gradient(color1, color2);
		}

		public String apply(String text) {
			int l = text.length();
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < l; i++) {
				builder.append(ChatColor.of(new Color(
						(color1.getColor().getRed() + (i * (1F / l) * (color2.getColor().getRed() - color1.getColor().getRed()))) / 255,
						(color1.getColor().getGreen() + (i * (1F / l) * (color2.getColor().getGreen() - color1.getColor().getGreen()))) / 255,
						(color1.getColor().getBlue() + (i * (1F / l) * (color2.getColor().getBlue() - color1.getColor().getBlue()))) / 255
				)));
				builder.append(text.charAt(i));
			}
			return builder.toString();
		}
	}

	public static class Rainbow {
		public static String apply(String text) {
			StringBuilder builder = new StringBuilder();
			int l = text.length();
			for (int i = 0; i < l; i++) {
				builder.append(ChatColor.of(Color.getHSBColor(((float) i / l) * .75F, .9F, .9F)));
				builder.append(text.charAt(i));
			}
			return builder.toString();
		}
	}

	public enum DefaultFontInfo {
		A('A', 5),
		a('a', 5),
		B('B', 5),
		b('b', 5),
		C('C', 5),
		c('c', 5),
		D('D', 5),
		d('d', 5),
		E('E', 5),
		e('e', 5),
		F('F', 5),
		f('f', 4),
		G('G', 5),
		g('g', 5),
		H('H', 5),
		h('h', 5),
		I('I', 3),
		i('i', 1),
		J('J', 5),
		j('j', 5),
		K('K', 5),
		k('k', 4),
		L('L', 5),
		l('l', 1),
		M('M', 5),
		m('m', 5),
		N('N', 5),
		n('n', 5),
		O('O', 5),
		o('o', 5),
		P('P', 5),
		p('p', 5),
		Q('Q', 5),
		q('q', 5),
		R('R', 5),
		r('r', 5),
		S('S', 5),
		s('s', 5),
		T('T', 5),
		t('t', 4),
		U('U', 5),
		u('u', 5),
		V('V', 5),
		v('v', 5),
		W('W', 5),
		w('w', 5),
		X('X', 5),
		x('x', 5),
		Y('Y', 5),
		y('y', 5),
		Z('Z', 5),
		z('z', 5),
		NUM_1('1', 5),
		NUM_2('2', 5),
		NUM_3('3', 5),
		NUM_4('4', 5),
		NUM_5('5', 5),
		NUM_6('6', 5),
		NUM_7('7', 5),
		NUM_8('8', 5),
		NUM_9('9', 5),
		NUM_0('0', 5),
		EXCLAMATION_POINT('!', 1),
		AT_SYMBOL('@', 6),
		NUM_SIGN('#', 5),
		DOLLAR_SIGN('$', 5),
		PERCENT('%', 5),
		UP_ARROW('^', 5),
		AMPERSAND('&', 5),
		ASTERISK('*', 5),
		LEFT_PARENTHESIS('(', 4),
		RIGHT_PERENTHESIS(')', 4),
		MINUS('-', 5),
		UNDERSCORE('_', 5),
		PLUS_SIGN('+', 5),
		EQUALS_SIGN('=', 5),
		LEFT_CURL_BRACE('{', 4),
		RIGHT_CURL_BRACE('}', 4),
		LEFT_BRACKET('[', 3),
		RIGHT_BRACKET(']', 3),
		COLON(':', 1),
		SEMI_COLON(';', 1),
		DOUBLE_QUOTE('"', 3),
		SINGLE_QUOTE('\'', 1),
		LEFT_ARROW('<', 4),
		RIGHT_ARROW('>', 4),
		QUESTION_MARK('?', 5),
		SLASH('/', 5),
		BACK_SLASH('\\', 5),
		LINE('|', 1),
		TILDE('~', 5),
		TICK('`', 2),
		PERIOD('.', 1),
		COMMA(',', 1),
		SPACE(' ', 3),
		DEFAULT('a', 4);

		private char character;
		private int length;

		DefaultFontInfo(char character, int length) {
			this.character = character;
			this.length = length;
		}

		public char getCharacter() {
			return this.character;
		}

		public int getLength() {
			return this.length;
		}

		public int getBoldLength() {
			if (this == DefaultFontInfo.SPACE)
				return this.getLength();
			return this.length + 1;
		}

		public static DefaultFontInfo getDefaultFontInfo(char c) {
			for (DefaultFontInfo dFI : DefaultFontInfo.values())
				if (dFI.getCharacter() == c)
					return dFI;
			return DefaultFontInfo.DEFAULT;
		}
	}

}
