package me.pugabyte.bncore.features.chat;

import me.pugabyte.bncore.features.chat.models.events.ChatEvent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.pugabyte.bncore.utils.StringUtils.countUpperCase;

public class Censor {

	public static void process(ChatEvent event) {
		String message = process(event.getMessage());

		// The following may make the message null
		message = dots(event.getOrigin(), message);
		// TODO: swear count

		if (message == null)
			event.setCancelled(true);
		else
			event.setMessage(message);
	}

	public static String process(String message) {
		message = dynmapLinkShorten(message);
		message = deUnicode(message);
		message = Emotes.process(message);
		message = dotCommand(message);
		message = lowercase(message);

		return message;
	}

	private static String lowercase(String message) {
		String characters = message.replaceAll(" ", "");
		int upper = countUpperCase(message);
		int pct = upper / characters.length() * 100;

		if (upper > 7 && pct > 40)
			message = message.toLowerCase();

		return message;
	}

	public static String dots(String origin, String message) {
		if (message.contains("(dot)") || message.contains("<dot>") || message.contains("{dot}") || message.contains("[dot]")) {
			Chat.broadcast("Prevented a possible advertisement attempt by " + origin, ": " + message);
			message = null;
		}

		return message;
	}

	public static String dotCommand(String message) {
		Pattern pattern = Pattern.compile("(\\ |^).\\/(\\/|)[a-zA-Z0-9\\-_]+");
		Matcher matcher = pattern.matcher(message);
		while (matcher.find()) {
			String group = matcher.group();
			String replace = group.replace("./", "/");
			message = message.replace(group, replace);
		}
		return message;
	}

	public static String dynmapLinkShorten(String message) {
		if (message.contains("map.bnn.gg")) {
			List<String> words = Arrays.asList(message.split(" "));

			for (String word : new ArrayList<>(words)) {
				word = word.toLowerCase();
				if (!word.contains("map.bnn.gg")) continue;

				word = word.replaceAll("http(s|)://", "");
				word = word.replaceAll("map\\.bnn\\.gg/\\?", "");
				String[] params = word.split("&");
				String world = null, x = null, z = null;
				for (String param : params)
					if (param.contains("worldname="))
						world = param.replaceAll("worldname=", "");
					else if (param.contains("x="))
						x = param.replaceAll("x=", "");
					else if (param.contains("z="))
						z = param.replaceAll("z=", "");

				if (world != null && x != null && z != null)
					message = message.replaceAll("\\?" + word, world +"/" + x + "/" + z);
			}
		}

		return message;
	}

	// Supports:
	// https://en.wikipedia.org/wiki/Halfwidth_and_Fullwidth_Forms_(Unicode_block)
	// ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ
	// ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ
	//
	// https://en.wikipedia.org/wiki/List_of_Unicode_characters#Enclosed_Alphanumerics
	// ⒜⒝⒞⒟⒠⒡⒢⒣⒤⒥⒦⒧⒨⒩⒪⒫⒬⒭⒮⒯⒰⒱⒲⒳⒴⒵
	// ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏ
	// ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ
	//
	// The decimal value for the first character of the real character sets
	// https://en.wikipedia.org/wiki/List_of_Unicode_characters#Basic_Latin

	private static final int LOWER = 97;
	private static final int UPPER = 65;

	public static String deUnicode(String input) {
		List<String> characters = Arrays.asList(input.split(" "));

		int index = 0;
		for (String character : new ArrayList<>(characters)) {
			if (character.length() == 0) continue;

			String unicode = toUnicode(character);

			characters.set(index, unicode);
			if (unicode.startsWith("\\uff")) {
				unicode = unicode.replaceAll("\\\\uff", "");
				int i = Integer.parseInt(unicode, 16);
				if (i >= 33 && i <= 58)
					i += UPPER - 33;
				else if (i >= 65 && i <= 90)
					i += LOWER - 65;

				String fixed = Integer.toString(i, 16);
				if (fixed.length() < 2)
					fixed = "0" + fixed;
				if (!fixed.equals(unicode))
					characters.set(index, "\\u00" + Integer.toString(i, 16));
			} else if (unicode.startsWith("\\u24")) {
				unicode = unicode.replaceAll("\\\\u24", "");
				int i = Integer.parseInt(unicode, 16);
				if (i >= 156 && i <= 181)
					i -= 156 - LOWER;
				else if (i >= 182 && i <= 207)
					i -= 182 - UPPER;
				else if (i >= 208 && i <= 233)
					i -= 208 - LOWER;

				String fixed = Integer.toString(i, 16);
				if (fixed.length() < 2)
					fixed = "0" + fixed;
				if (!fixed.equals(unicode))
					characters.set(index, "\\u00" + Integer.toString(i, 16));
			}

			characters.set(index, new String(characters.get(index).getBytes(), StandardCharsets.UTF_8));

			++index;
		}

		return String.join("", characters);
	}

	public static String toUnicode(String input) {
		return toUnicode(input.toCharArray());
	}

	public static String toUnicode(char[] input) {
		StringBuilder output = new StringBuilder();
		for (char c : input)
			output.append("\\u").append(Integer.toHexString(c | 0x10000).substring(1));
		return output.toString();
	}
}

