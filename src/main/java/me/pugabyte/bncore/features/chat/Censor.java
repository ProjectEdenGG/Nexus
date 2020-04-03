package me.pugabyte.bncore.features.chat;

import me.pugabyte.bncore.features.chat.events.ChatEvent;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.pugabyte.bncore.utils.StringUtils.countUpperCase;

public class Censor {

	public static void process(ChatEvent event) {
		dynmapLinkShorten(event);
		deUnicode(event);
		Emotes.process(event);
		dotCommand(event);
		lowercase(event);
		dots(event);

		// TODO: swear count
	}

	private static void lowercase(ChatEvent event) {
		String message = event.getMessage();
		String characters = message.replaceAll(" ", "");
		if (characters.length() == 0)
			return;
		int upper = countUpperCase(message);
		int pct = upper / characters.length() * 100;

		if (upper > 7 && pct > 40)
			message = message.toLowerCase();

		event.setMessage(message);
	}

	public static void dots(ChatEvent event) {
		if (event.getMessage().toLowerCase().matches("(\\(|<|\\{|\\[)dot(]|}|>|\\))")) {
			Chat.broadcast("Prevented a possible advertisement attempt by " + event.getOrigin() + ": " + event.getMessage(), "Staff");
			event.setCancelled(true);
		}
	}

	public static void dotCommand(ChatEvent event) {
		String message = event.getMessage();
		Pattern pattern = Pattern.compile("(\\ |^).\\/(\\/|)[a-zA-Z0-9\\-_]+");
		Matcher matcher = pattern.matcher(message);
		while (matcher.find()) {
			String group = matcher.group();
			String replace = group.replace("./", "/");
			message = message.replace(group, replace);
		}
		event.setMessage(message);
	}

	public static void dynmapLinkShorten(ChatEvent event) {
		String message = event.getMessage();
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

		event.setMessage(message);
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

	public static void deUnicode(ChatEvent event) {
		String message = event.getMessage();
		List<String> characters = Arrays.asList(message.split(""));

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

			characters.set(index, StringEscapeUtils.unescapeJava(characters.get(index)));

			++index;
		}

		event.setMessage(String.join("", characters));
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

