package gg.projecteden.nexus.features.minigolf.models;

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

	private final int modelId;
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
