package gg.projecteden.nexus.features.minigames.models.perks.common;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface HatMaterialPerk extends HatPerk {
	Material getMaterial();

	@Override
	default @NotNull ItemStack getItem() {
		return new ItemStack(getMaterial());
	}
}
