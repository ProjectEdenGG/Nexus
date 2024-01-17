package gg.projecteden.nexus.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.Style;

import java.util.List;

public class FontUtils {

	public final static List<String> MINUS_CHARS = List.of("ꈁ", "麖", "ꈂ", "ꈃ", "ꈄ", "ꈅ", "ꈆ", "ꈇ", "ꈈ", "ꈉ");

	public static String minus(int number) {
		int tens = number / 10;
		int modulo = number % 10;
		return MINUS_CHARS.get(9).repeat(tens) + (modulo > 0 ? MINUS_CHARS.get(modulo - 1) : "");
	}

	public static String getMenuTexture(String textureChar, int rows) {
		return getMenuTexture(10, textureChar, rows);
	}

	public static String getNextMenuTexture(String textureChar, int rows) {
		return getMenuTexture(9, textureChar, rows);
	}

	private static String getMenuTexture(int minus, String textureChar, int rows) {
		String title = minus(minus) + "&f" + textureChar;

		// TODO: figure out all other row spacings
		if (rows == 3) return title + minus(213); // 3

		return title + minus(214); // 6
	}

	@AllArgsConstructor
	public enum FontType {
		DEFAULT("minecraft:default"),
		ACTION_BAR_LINE_1("minecraft:actionbar_line1"),
		PROFILE_TITLE("minecraft:profile_title")
		;

		private final String key;

		public Key getFont() {
			return Key.key(this.key);
		}

		public Style getStyle() {
			return Style.style().font(this.getFont()).build();
		}
	}

	@Getter
	@AllArgsConstructor
	public enum FontChar {
		BLACK_SCREEN("鄜"),
		RED_SCREEN_20_PERCENT_OPACITY("滍")
		;

		private final String character;

	}

}
