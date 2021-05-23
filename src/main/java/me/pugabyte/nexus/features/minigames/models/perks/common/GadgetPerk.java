package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface GadgetPerk extends Perk {
	default void tick(Player player, int slot) {
		player.getInventory().setItem(slot, getItem());
	}

	default ItemStack basicItem(Material material) {
		return new ItemBuilder(material).name("&3" + getName()).build();
	}

	ItemStack getItem();

	@Override
	default @NotNull ItemStack getMenuItem() {
		return getItem();
	}

	@Override
	default @NotNull PerkCategory getPerkCategory() {
		return PerkCategory.GADGET;
	}

	void useGadget(Player player);

	default boolean cancelEvent() {
		return true;
	}

	default int getCooldown() {
		return 0;
	}
}
