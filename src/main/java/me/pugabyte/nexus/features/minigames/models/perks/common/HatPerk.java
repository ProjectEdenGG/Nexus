package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
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

	default @NotNull Map<EnumItemSlot, ItemStack> getLoadout() {
		Map<EnumItemSlot, ItemStack> loadout = new HashMap<>();
		loadout.put(EnumItemSlot.HEAD, getItem());
		return loadout;
	}

	@NotNull ItemStack getItem();

	@Override
	default @NotNull ItemStack getMenuItem() {
		return getItem();
	}
}
