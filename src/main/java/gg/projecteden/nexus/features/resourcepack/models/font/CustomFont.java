package gg.projecteden.nexus.features.resourcepack.models.font;

import lombok.AllArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.Style;

@AllArgsConstructor
public enum CustomFont {
	DEFAULT("minecraft:default"),
	ACTION_BAR_LINE_1("minecraft:actionbar_line1"),
	PROFILE_TITLE("minecraft:profile_title"),
	;

	private final String key;

	public Key getFont() {
		return Key.key(this.key);
	}

	public Style getStyle() {
		return Style.style().font(this.getFont()).build();
	}
}
