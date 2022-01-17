package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public enum CreeperVariant implements MobHeadVariant {
	NONE,
	POWERED,
	;

	@Setter
	private ItemStack itemStack;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.CREEPER;
	}

	public static CreeperVariant of(Creeper creeper) {
		return creeper.isPowered() ? POWERED : NONE;
	}
}
