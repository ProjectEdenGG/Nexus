package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.MushroomCow.Variant;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum MooshroomVariant implements MobHeadVariant {
	RED(Variant.RED),
	BROWN(Variant.BROWN),
	;

	private final Variant bukkitType;
	@Setter
	private ItemStack itemStack;

	@Override
	public EntityType getEntityType() {
		return EntityType.MUSHROOM_COW;
	}

	public static MooshroomVariant of(MushroomCow mushroomCow) {
		return Arrays.stream(values()).filter(entry -> mushroomCow.getVariant() == entry.getBukkitType()).findFirst().orElse(null);
	}
}
