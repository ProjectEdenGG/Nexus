package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum HorseColor implements MobHeadVariant {
	WHITE(Horse.Color.WHITE),
	CREAMY(Horse.Color.CREAMY),
	CHESTNUT(Horse.Color.CHESTNUT),
	BROWN(Horse.Color.BROWN),
	BLACK(Horse.Color.BLACK),
	GRAY(Horse.Color.GRAY),
	DARK_BROWN(Horse.Color.DARK_BROWN),
	;

	private final Horse.Color type;
	@Setter
	private ItemStack itemStack;

	@Override
	public EntityType getEntityType() {
		return EntityType.HORSE;
	}

	public static HorseColor of(Horse horse) {
		return Arrays.stream(values()).filter(entry -> horse.getColor() == entry.getType()).findFirst().orElse(null);
	}
}
