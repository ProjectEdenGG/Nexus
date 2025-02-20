package gg.projecteden.nexus.features.resourcepack.models.font;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
public enum CustomTexture {
	GUI_TOOL_MODIFICATION_TABLE("犁"),
	GUI_ENCHANTED_BOOK_SPLITTER("千"),
	GUI_DYE_STATION("섈"),
	GUI_DYE_STATION_COSTUME("膛"),
	GUI_DYE_STATION_CREATIVE("域"),

	GUI_PUGMAS21_ADVENT_1("盆"),
	GUI_PUGMAS21_ADVENT_2("鉊"),
	GUI_PUGMAS24_ADVENT_1("丫"),
	GUI_PUGMAS24_ADVENT_2("勹"),
	GUI_PUGMAS25_ADVENT_1("?"),
	GUI_PUGMAS25_ADVENT_2("?"),

	GUI_PROFILE("升"),
	GUI_PROFILE_BACKGROUND("顼"),
	//
	GUI_PROFILE_TEXTURE_DOTS("夤"),
	GUI_PROFILE_TEXTURE_SHINE("疱"),
	GUI_PROFILE_TEXTURE_STRIPES_VERTICAL("祯"),
	GUI_PROFILE_TEXTURE_SPLIT("臭"),
	GUI_PROFILE_TEXTURE_CIRCUIT("糊"),
	GUI_PROFILE_TEXTURE_GINGHAM("癃"),
	GUI_PROFILE_TEXTURE_ZEBRA("硅"),
	//
	GUI_PROFILE_IMAGE_TEST("糖"),
	GUI_PROFILE_IMAGE_CATS("诅"),
	GUI_PROFILE_IMAGE_BIRTHDAY("孟"),
	GUI_PROFILE_IMAGE_BEES("脒"),

	GUI_PROFILE_RANK_UNKNOWN("笞"),
	GUI_PROFILE_RANK_GUEST("砫"),
	GUI_PROFILE_RANK_MEMBER("鼫"),
	GUI_PROFILE_RANK_TRUSTED("廒"),
	GUI_PROFILE_RANK_ELITE("婪"),
	GUI_PROFILE_RANK_VETERAN("愆"),
	GUI_PROFILE_RANK_NOBLE("棽"),
	GUI_PROFILE_RANK_BUILDER("所"),
	GUI_PROFILE_RANK_ARCHITECT("砗"),
	GUI_PROFILE_RANK_MODERATOR("超"),
	GUI_PROFILE_RANK_OPERATOR("笸"),
	GUI_PROFILE_RANK_ADMIN("棘"),
	GUI_PROFILE_RANK_OWNER("爷"),
	GUI_PROFILE_ARMOR_HELMET("委"),
	GUI_PROFILE_ARMOR_CHESTPLATE("晕"),
	GUI_PROFILE_ARMOR_LEGGINGS("鸱"),
	GUI_PROFILE_ARMOR_BOOTS("粞"),
	GUI_PROFILE_COSTUME_HAT("疕"),
	GUI_PROFILE_COSTUME_HAND("楂"),

	GUI_CONFIRMATION("禧", 3),
	GUI_CONFIRMATION_SLOT("埤", 3),
	GUI_CRAFTING_RECIPE("魁", 3),
	GUI_SMELTING_RECIPE("糯", 3),

	MINIGAMES_MENU_SEPARATOR("敷"),

	;

	@NonNull
	@Getter
	final String fontChar;
	int rows = 6;

	CustomTexture(@NotNull String fontChar, int rows) {
		this.fontChar = fontChar;
		this.rows = rows;
	}

	public String getChar() {
		return fontChar;
	}

	public String getMenuTexture() {
		return getMenuTexture(this.rows);
	}

	public String getMenuTexture(int rows) {
		return getMenuTexture(fontChar, rows);
	}

	public String getMenuTexture(ChatColor color, int rows) {
		return getMenuTexture(fontChar, color, rows);
	}

	public String getNextMenuTexture() {
		return getNextMenuTexture(this.rows);
	}

	public String getNextMenuTexture(int rows) {
		return getNextMenuTexture(fontChar, rows);
	}

	public String getNextMenuTexture(ChatColor color, int rows) {
		return getNextMenuTexture(fontChar, color, rows);
	}

	//

	public final static List<String> MINUS_CHARS = List.of("ꈁ", "麖", "ꈂ", "ꈃ", "ꈄ", "ꈅ", "ꈆ", "ꈇ", "ꈈ", "ꈉ");

	public static String minus(int number) {
		int tens = number / 10;
		int modulo = number % 10;
		return MINUS_CHARS.get(9).repeat(tens) + (modulo > 0 ? MINUS_CHARS.get(modulo - 1) : "");
	}

	private String getMenuTexture(String textureChar, int rows) {
		return getMenuTexture(10, textureChar, rows);
	}

	private String getMenuTexture(String textureChar, ChatColor color, int rows) {
		return getMenuTexture(10, textureChar, color, rows);
	}

	private static String getNextMenuTexture(String textureChar, int rows) {
		return getMenuTexture(9, textureChar, rows);
	}

	private static String getMenuTexture(int minus, String textureChar, int rows) {
		return getMenuTexture(minus, textureChar, ChatColor.WHITE, rows);
	}

	private static String getNextMenuTexture(String textureChar, ChatColor color, int rows) {
		return getMenuTexture(9, textureChar, color, rows);
	}

	public static String getMenuTexture(int minus, String textureChar, ChatColor color, int rows) {
		String title = minus(minus) + color + textureChar;

		// TODO: figure out all other row spacings
		if (rows == 3) return title + minus(213); // 3 rows

		return title + minus(214); // 6 rows
	}

	//

	private static final String SCROLL_BACKGROUND = "久";
	private static final List<List<String>> SCROLL_INDEXES = List.of(
			List.of("魉"),
			List.of("辆", "沩"),
			List.of("漷", "秬", "籽"),
			List.of("醭", "泽", "转", "洼"),
			List.of("髌", "泗", "穙", "邸", "甬"),
			List.of("粽", "轩", "乏", "袭", "说", "魋"),
			List.of("廋", "糠", "稿", "膑", "配", "丸", "蝻"),
			List.of("程", "磉", "暿", "飗", "毪", "轵", "浬", "腒"),
			List.of("骷", "淟", "貉", "陇", "鲌", "砵", "蚯", "涞", "轮"),
			List.of("晌", "夐", "暝", "赳", "盩", "墘", "貌", "糈", "疍", "糅")
	);

	public static String getScrollTitle(int pages, int page) {
		return "&f" +
				CustomTexture.minus(10) +
				CustomTexture.SCROLL_BACKGROUND +
				CustomTexture.minus(33) +
				CustomTexture.SCROLL_INDEXES.get(pages - 1).get(page) +
				CustomTexture.minus(200);
	}
}
