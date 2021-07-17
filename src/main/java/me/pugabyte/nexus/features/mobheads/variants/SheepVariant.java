package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import me.pugabyte.nexus.utils.ColorType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;

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
	public EntityType getEntityType() {
		return EntityType.SHEEP;
	}

	public static SheepVariant of(Sheep sheep) {
		return Arrays.stream(values()).filter(entry -> ColorType.of(sheep.getColor()) == entry.getBukkitType()).findFirst().orElse(null);
	}
}
