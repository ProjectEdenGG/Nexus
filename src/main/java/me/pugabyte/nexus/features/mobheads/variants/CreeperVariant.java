package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public enum CreeperVariant implements MobHeadVariant {
	POWERED,
	;

	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.CREEPER;
	}

	public static CreeperVariant of(Creeper creeper) {
		return creeper.isPowered() ? POWERED : null;
	}
}
