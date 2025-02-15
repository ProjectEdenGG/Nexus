package gg.projecteden.nexus.features.minigolf.models;

import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
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
	WHITE(ItemModelType.MINIGOLF_BALL, ColorType.WHITE),
	BLACK(ItemModelType.MINIGOLF_BALL_BLACK, ColorType.BLACK),
	RED(ItemModelType.MINIGOLF_BALL_RED, ColorType.LIGHT_RED),
	ORANGE(ItemModelType.MINIGOLF_BALL_ORANGE, ColorType.ORANGE),
	YELLOW(ItemModelType.MINIGOLF_BALL_YELLOW, ColorType.YELLOW),
	GREEN(ItemModelType.MINIGOLF_BALL_GREEN, ColorType.LIGHT_GREEN),
	LIGHT_BLUE(ItemModelType.MINIGOLF_BALL_LIGHT_BLUE, ColorType.LIGHT_BLUE),
	BLUE(ItemModelType.MINIGOLF_BALL_BLUE, ColorType.BLUE),
	PURPLE(ItemModelType.MINIGOLF_BALL_PURPLE, ColorType.PURPLE),
	PINK(ItemModelType.MINIGOLF_BALL_PINK, ColorType.PINK),
	RAINBOW(ItemModelType.MINIGOLF_BALL_RAINBOW, ColorType.WHITE);

	private final ItemModelType model;
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
