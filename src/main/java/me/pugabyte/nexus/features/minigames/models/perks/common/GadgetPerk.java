package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class GadgetPerk extends Perk {
	public void tick(Player player, int slot) {
		player.getInventory().setItem(slot, getItem());
	}

	protected ItemStack basicItem(Material material) {
		return new ItemBuilder(material).name("&3" + getName()).build();
	}

	public abstract ItemStack getItem();

	@Override
	public ItemStack getMenuItem() {
		return getItem();
	}

	@Override
	public PerkCategory getPerkCategory() {
		return PerkCategory.GADGET;
	}

	public void useGadget(Player player) {}

	public boolean cancelEvent() {
		return true;
	}

	public int getCooldown() {
		return 0;
	}
}
