package gg.projecteden.nexus.features.minigolf.models;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ColorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Color;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/*
	TODO:
		Country flags
		Earth
		Project Eden logo
		NASA
		Starbucks
		Beach ball
		8-ball
		Disco ball
		Watermelon
		Christmas ornament
		Player heads
		Armadillo (for bri specifically)

		- Custom sounds?
		- Custom sink animations?
 */

@Getter
@AllArgsConstructor
public enum GolfBallStyle {
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

	@Unlockable
	RAINBOW(ItemModelType.MINIGOLF_BALL_RAINBOW) {
		@Override
		public List<Color> getFireworkColors() {
			return EnumUtils.valuesExcept(GolfBallStyle.class, RAINBOW, WHITE, BLACK).stream()
				.map(color -> color.getFireworkColor().getBukkitColor())
				.toList();
		}
	};

	GolfBallStyle(ItemModelType model) {
		this.model = model;
		this.fireworkColor = null;
	}

	private final ItemModelType model;
	private final ColorType fireworkColor;

	public List<Color> getFireworkColors() {
		var color = fireworkColor;

		if (color == null)
			color = ColorType.WHITE;

		return Collections.singletonList(color.getBukkitColor());
	}

	public boolean isDefault() {
		boolean unlockable = getField().isAnnotationPresent(Unlockable.class);
		boolean purchasable = getField().isAnnotationPresent(Purchasable.class);
		return !unlockable && !purchasable;
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Unlockable {}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Purchasable {}


}
