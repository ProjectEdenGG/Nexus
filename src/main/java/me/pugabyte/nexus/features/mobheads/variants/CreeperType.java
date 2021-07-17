package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public enum CreeperType implements MobHeadVariant {
	POWERED,
	;

	@Setter
	private ItemStack itemStack;

	@Override
	public EntityType getEntityType() {
		return EntityType.CREEPER;
	}

	public static CreeperType of(Creeper creeper) {
		return creeper.isPowered() ? POWERED : null;
	}
}
