package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum HorseVariant implements MobHeadVariant {
	WHITE(Color.WHITE),
	CREAMY(Color.CREAMY),
	CHESTNUT(Color.CHESTNUT),
	BROWN(Color.BROWN),
	BLACK(Color.BLACK),
	GRAY(Color.GRAY),
	DARK_BROWN(Color.DARK_BROWN),
	;

	private final Color bukkitType;
	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.HORSE;
	}

	public static HorseVariant of(Horse horse) {
		return Arrays.stream(values()).filter(entry -> horse.getColor() == entry.getBukkitType()).findFirst().orElse(null);
	}
}
