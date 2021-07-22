package gg.projecteden.nexus.features.minigames.models.perks.common;

import com.comphenix.protocol.wrappers.EnumWrappers;
import gg.projecteden.nexus.features.minigames.models.perks.PerkCategory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A perk that gives a user a fake armor item on their head
 */
public interface HatPerk extends LoadoutPerk {
	@Override
	default @NotNull PerkCategory getPerkCategory() {
		return PerkCategory.HAT;
	}

	default @NotNull Map<EnumWrappers.ItemSlot, ItemStack> getLoadout() {
		Map<EnumWrappers.ItemSlot, ItemStack> loadout = new HashMap<>();
		loadout.put(EnumWrappers.ItemSlot.HEAD, getItem());
		return loadout;
	}

	@NotNull ItemStack getItem();

	@Override
	default @NotNull ItemStack getMenuItem() {
		return getItem();
	}
}
