package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import gg.projecteden.nexus.utils.ColorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SheepVariant implements MobHeadVariant {
	RED("3912", ColorType.RED),
	ORANGE("3899", ColorType.ORANGE),
	YELLOW("3902", ColorType.YELLOW),
	LIGHT_GREEN("3903", ColorType.LIGHT_GREEN),
	GREEN("3914", ColorType.GREEN),
	CYAN("3907", ColorType.CYAN),
	LIGHT_BLUE("3901", ColorType.LIGHT_BLUE),
	BLUE("3909", ColorType.BLUE),
	PURPLE("3908", ColorType.PURPLE),
	MAGENTA("3900", ColorType.MAGENTA),
	PINK("3915", ColorType.PINK),
	BROWN("3910", ColorType.BROWN),
	BLACK("3913", ColorType.BLACK),
	GRAY("3905", ColorType.GRAY),
	LIGHT_GRAY("3906", ColorType.LIGHT_GRAY),
	WHITE("334", ColorType.WHITE),
	;

	private final String headId;
	private final ColorType bukkitType;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.SHEEP;
	}

	public static SheepVariant of(Sheep sheep) {
		return Arrays.stream(values()).filter(entry -> ColorType.of(sheep.getColor()) == entry.getBukkitType()).findFirst().orElse(null);
	}
}
