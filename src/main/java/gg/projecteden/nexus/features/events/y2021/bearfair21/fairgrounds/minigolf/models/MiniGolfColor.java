package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models;

import gg.projecteden.nexus.utils.ColorType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MiniGolfColor {
	WHITE(901, ColorType.WHITE),
	BLACK(902, ColorType.BLACK),
	RED(903, ColorType.LIGHT_RED),
	ORANGE(904, ColorType.ORANGE),
	YELLOW(905, ColorType.YELLOW),
	GREEN(906, ColorType.LIGHT_GREEN),
	LIGHT_BLUE(907, ColorType.LIGHT_BLUE),
	BLUE(908, ColorType.BLUE),
	PURPLE(909, ColorType.PURPLE),
	PINK(910, ColorType.PINK),
	RAINBOW(911, ColorType.WHITE);

	@Getter
	private final int modelId;
	@Getter
	private final ColorType colorType;
}
