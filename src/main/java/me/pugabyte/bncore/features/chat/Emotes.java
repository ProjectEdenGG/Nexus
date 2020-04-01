package me.pugabyte.bncore.features.chat;

import lombok.Getter;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Emotes {
	CHECKMARK("&2✔", ":yes:"),
	X("&c✖", ":no:"),
	COFFEE("&6☕", ":coffee:"),
	TEA("&6☕", ":tea:"),
	ARROW("&2➜", ":arrow:"),
	SNOWFLAKE("&b❊", ":snowflake:"),
	SNOWMAN("&b☃", ":snowman:"),
	MUSIC1("&5♪", ":music1:"),
	MUSIC2("&5♫", ":music2:"),
	MUSIC3("&5♩", ":music3:"),
	MUSIC4("&5♬", ":music4:"),
	NOU("&2↙↗", ":nou:"),
	YAY("&6\\&eo&6/", "\\o/"),
	WAVING_RIGHT("&d( &dﾟ◡ﾟ)/", "o/"),
	WAVING_LEFT("&b\\(ﾟ◡ﾟ &b)", "\\o"),
	SALUTE1("&eo&6>", "o>"),
	SALUTE2("&eo&67", "o7"),
	SHRUG("&e¯\\_(ツ)_/¯", ":shrug:"),
	FACEPALM("&e(&6ლ&e‸－)", ":facepalm:"),
	TABLEFLIP("&c(╯°□°）╯&7︵ &6┻━┻", ":tableflip:"),
	ANGRY("&c┗(&4`&cO&4'&c)┛", ":angry:"),
	HAPPY("&e(&d✿&e◠‿◠)", ":happy:"),
	FLOWER("&d⚘", ":flower:"),
	TYPING("&6✎&8...", ":typing:"),
	CONFUSED("&e(&30&e.&3o&6?&e)", ":confused:"),
	SNAIL("&e@&a'&e-&a'", ":snail:"),
	DROPBEAR("&7ʕ&c•&7ᴥ&c•&7ʔ", ":dropbear:"),
	FIGHTME("&e(ง'-')ง", ":fightme:"),
	MONEY("&2[$&a100&2$]", ":money:"),
	BIRD("&3\\(&f˘&6▾&f˘&3)/", ":bird:"),
	FISH("&b<º))))><", "<><"),
	EYES("&b◔&e‿ &b◔", ":eyes:"),
	HEART("&4❤", ":heart:"),
	HEART_COLORED("❤", "<3", Arrays.asList(
			ChatColor.RED,
			ChatColor.LIGHT_PURPLE,
			ChatColor.DARK_PURPLE,
			ChatColor.BLACK,
			ChatColor.AQUA,
			ChatColor.GREEN));

	@Getter
	private String emote;
	@Getter
	private String key;
	@Getter
	private List<ChatColor> colors = new ArrayList<>();

	Emotes(String emote, String key) {
		this.emote = emote;
		this.key = key;
	}

	Emotes(String emote, String key, List<ChatColor> colors) {
		this.emote = emote;
		this.key = key;
		this.colors = colors;
	}

	public static String process(String input) {
		for (Emotes value : values())
			while (input.contains(value.getKey())) {
				String result = value.getEmote();

				if (value.getColors().size() > 0)
					result = Utils.getRandomElement(value.getColors()) + result;

				input = input.replaceFirst(value.getKey(), result + "&f");
			}
		return input;
	}
}
