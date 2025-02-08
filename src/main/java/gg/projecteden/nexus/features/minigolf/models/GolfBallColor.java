package gg.projecteden.nexus.features.minigolf.models;

import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ColorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public enum GolfBallColor {
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

	private final CustomMaterial model;
	private final ColorType colorType;

	public List<Color> getFireworkColors() {
		if (this == RAINBOW) {
			List<GolfBallColor> ignore = Arrays.asList(RAINBOW, WHITE, BLACK);
			return Arrays.stream(values())
					.filter(color -> !ignore.contains(color))
					.map(color -> color.getColorType().getBukkitColor())
					.toList();
		}

		return Collections.singletonList(colorType.getBukkitColor());
	}

}
