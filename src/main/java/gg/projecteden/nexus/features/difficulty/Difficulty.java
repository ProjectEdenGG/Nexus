package gg.projecteden.nexus.features.difficulty;

import gg.projecteden.nexus.models.difficulty.DifficultyUserService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

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

	public boolean gte(Difficulty difficulty) {
		return ordinal() >= difficulty.ordinal();
	}

	public boolean isNormal() {
		return gte(NORMAL);
	}

	public boolean isMedium() {
		return gte(MEDIUM);
	}

	public boolean isHard() {
		return gte(HARD);
	}
}
