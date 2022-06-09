package gg.projecteden.nexus.features.mobheads.variants;

import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public enum CreeperVariant implements MobHeadVariant {
	NONE(null) {
		@Override
		public @NotNull ItemStack getItemStack() {
			return new ItemStack(Material.CREEPER_HEAD);
		}
	},
	POWERED("6450"),
	;

	private final String headId;

	@Override
	public @NotNull EntityType getEntityType() {
		return EntityType.CREEPER;
	}

	public static CreeperVariant of(Creeper creeper) {
		return creeper.isPowered() ? POWERED : NONE;
	}
}
