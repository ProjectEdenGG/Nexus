package me.pugabyte.nexus.features.minigames.models.perks;

import org.bukkit.inventory.ItemStack;

public abstract class Perk {
	public abstract String getName();
	public abstract ItemStack getMenuItem();
	public abstract String[] getDescription();
	public abstract PerkCategory getCategory();
	public abstract int getPrice();
}
