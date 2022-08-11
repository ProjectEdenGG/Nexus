package gg.projecteden.nexus.features.survival.difficulty;

import gg.projecteden.nexus.models.difficulty.DifficultyUserService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Arrays;

@AllArgsConstructor
public enum Difficulty {
	NORMAL(ColorType.LIGHT_GREEN),
	MEDIUM(ColorType.ORANGE),
	HARD(ColorType.LIGHT_RED),
	;

	private final ColorType color;

	private static final DifficultyUserService service = new DifficultyUserService();

	public String getName() {
		return StringUtils.camelCase(this.name());
	}

	public String getColoredName() {
		return color.getChatColor() + getName();
	}

	public static Difficulty of(Player player) {
		return service.get(player).getDifficulty();
	}

	public boolean isApplicable(Object object) {
		return isApplicable(object.getClass());
	}

	public boolean isApplicable(Class<?> clazz) {
		final ForDifficulty annotation = clazz.getAnnotation(ForDifficulty.class);
		return annotation != null && Arrays.asList(annotation.value()).contains(this);
	}

	public boolean gte(Difficulty difficulty) {
		return ordinal() >= difficulty.ordinal();
	}

	public boolean isNormalOrHigher() {
		return gte(NORMAL);
	}

	public boolean isMediumOrHigher() {
		return gte(MEDIUM);
	}

	public boolean isHardOrHigher() {
		return gte(HARD);
	}
}
