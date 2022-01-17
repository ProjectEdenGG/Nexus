package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import gg.projecteden.nexus.utils.ColorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SheepVariant implements MobHeadVariant {
	RED(ColorType.RED),
	ORANGE(ColorType.ORANGE),
	YELLOW(ColorType.YELLOW),
	LIGHT_GREEN(ColorType.LIGHT_GREEN),
	GREEN(ColorType.GREEN),
	CYAN(ColorType.CYAN),
	LIGHT_BLUE(ColorType.LIGHT_BLUE),
	BLUE(ColorType.BLUE),
	PURPLE(ColorType.PURPLE),
	MAGENTA(ColorType.MAGENTA),
	PINK(ColorType.PINK),
	BROWN(ColorType.BROWN),
	BLACK(ColorType.BLACK),
	GRAY(ColorType.GRAY),
	LIGHT_GRAY(ColorType.LIGHT_GRAY),
	WHITE(ColorType.WHITE),
	;

	private final ColorType bukkitType;
	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.SHEEP;
	}

	public static SheepVariant of(Sheep sheep) {
		return Arrays.stream(values()).filter(entry -> ColorType.of(sheep.getColor()) == entry.getBukkitType()).findFirst().orElse(null);
	}
}
