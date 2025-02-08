package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models;

import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ColorType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MiniGolfColor {
	WHITE(CustomMaterial.MINIGOLF_BALL, ColorType.WHITE),
	BLACK(CustomMaterial.MINIGOLF_BALL_BLACK, ColorType.BLACK),
	RED(CustomMaterial.MINIGOLF_BALL_RED, ColorType.LIGHT_RED),
	ORANGE(CustomMaterial.MINIGOLF_BALL_ORANGE, ColorType.ORANGE),
	YELLOW(CustomMaterial.MINIGOLF_BALL_YELLOW, ColorType.YELLOW),
	GREEN(CustomMaterial.MINIGOLF_BALL_GREEN, ColorType.LIGHT_GREEN),
	LIGHT_BLUE(CustomMaterial.MINIGOLF_BALL_LIGHT_BLUE, ColorType.LIGHT_BLUE),
	BLUE(CustomMaterial.MINIGOLF_BALL_BLUE, ColorType.BLUE),
	PURPLE(CustomMaterial.MINIGOLF_BALL_PURPLE, ColorType.PURPLE),
	PINK(CustomMaterial.MINIGOLF_BALL_PINK, ColorType.PINK),
	RAINBOW(CustomMaterial.MINIGOLF_BALL_RAINBOW, ColorType.WHITE);

	@Getter
	private final CustomMaterial model;
	@Getter
	private final ColorType colorType;
}
