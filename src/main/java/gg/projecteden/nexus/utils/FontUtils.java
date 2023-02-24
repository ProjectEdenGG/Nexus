package gg.projecteden.nexus.utils;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.Style;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;

public class FontUtils {

	public final static List<String> MINUS_CHARS = List.of("ꈁ", "麖", "ꈂ", "ꈃ", "ꈄ", "ꈅ", "ꈆ", "ꈇ", "ꈈ", "ꈉ");;

	public static String minus(int number) {
		int tens = number / 10;
		int modulo = number % 10;
		return MINUS_CHARS.get(9).repeat(tens) + (modulo > 0 ? MINUS_CHARS.get(modulo - 1) : "");
	}

	@AllArgsConstructor
	public enum FontType {
		DEFAULT("minecraft:default"),
		TOOL_TIP_LINE_1("minecraft:tooltip_line1"),
		TOOL_TIP_LINE_2("minecraft:tooltip_line2"),
		TOOL_TIP_LINE_3("minecraft:tooltip_line3"),
		;

		private final String key;

		public Key getFont() {
			return Key.key(this.key);
		}

		public Style getStyle() {
			return Style.style().font(this.getFont()).build();
		}
	}

	private static int getLongest(@NonNull List<String> strings) {
		return strings.stream().max(Comparator.comparingInt(String::length)).orElseThrow().length();
	}

	public static JsonBuilder getToolTip(String line1, String line2, String line3, int additionalSpaces, Player debugger) {
		int longest = getLongest(List.of(line1, line2, line3));

		debugger.sendMessage("Longest: " + longest);

		// Background
		int toolTipSpaces = 42; // TODO: DYNAMIC
		debugger.sendMessage("BG Spaces: " + toolTipSpaces);
		String tooltipSpacing = " ".repeat(toolTipSpaces);


		int centerChars = (int) (longest * 3.78947);
		debugger.sendMessage("BG Chars: " + centerChars);

		String left = "廒ꈄꢷ";
		String right = "ꈄꢸ";
		String center = "ꈄꢹ".repeat(centerChars);

		// Lines
		int line2Spaces = (int) (line1.length() * 1.35);
		debugger.sendMessage("Line2 Spaces: " + line2Spaces);

		int line3Spaces = (int) (line2.length() * 1.25); // TODO: not perfect
		debugger.sendMessage("Line3 Spaces: " + line3Spaces);

		String spacingLine1 = " ꈄ";
		String spacingLine2 = "ꈄ".repeat(line2Spaces); // 16  -- "Wooden bench"
		String spacingLine3 = "ꈄ".repeat(line3Spaces); // 12 -- "Price: 250"

		// Json
		return new JsonBuilder()
			.next(tooltipSpacing).group()
			.next(left + center + right).group()

			.next(spacingLine1).group()
			.next(line1).style(FontType.TOOL_TIP_LINE_1.getStyle()).group()

			.next(spacingLine2).group()
			.next(line2).style(FontType.TOOL_TIP_LINE_2.getStyle()).group()

			.next(spacingLine3).group()
			.next(line3).style(FontType.TOOL_TIP_LINE_3.getStyle()).group();
	}


	/*
	title @p subtitle subtitle {"text":"ꈄꢸꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢷ"}

	title @p subtitle subtitle {"text":"Test1", "extra":[{"text":"Test2","font":"minecraft:tooltip_line1"}]} ;;

	title @p subtitle {"text":"ꈄꢷꈄꢹꈄꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢸ"}

	title @p subtitle {"text":"                 ꢸꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢹꈂꢷ"}

	title @p subtitle {"text":"                                           ",
		"extra":[{"text":"廒ꈄꢷꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢸ"}]}

	title @p subtitle {"text":"                                           ",
		"extra":[{"text":"廒ꈄꢷꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢹꈄꢸ"},
			{"text":" ꈄ"},{"text":"Wooden bench","font":"minecraft:tooltip_line1"},
			{"text":"ꈄꈄꈄꈄꈄꈄꈄꈄꈄꈄꈄꈄꈄꈄꈄꈄ"},{"text":"Price: ","color":"gray","font":"minecraft:tooltip_line2"},{"text":"250","font":"minecraft:tooltip_line2"},
			{"text":"ꈄꈄꈄꈄꈄꈄꈄꈄꈄꈄꈄꈄ"},{"text":"(Click to purchase)","color":"gold","font":"minecraft:tooltip_line3"}
		]
	}



 */

}
