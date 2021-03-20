package me.pugabyte.nexus.features.minigames.models.perks;

import lombok.EqualsAndHashCode;
import org.bukkit.inventory.ItemStack;

@EqualsAndHashCode
public abstract class Perk {
	@EqualsAndHashCode.Include
	public abstract String getName();
	public abstract ItemStack getMenuItem();
	public abstract String[] getDescription();
	@EqualsAndHashCode.Include
	public abstract PerkCategory getCategory();
	@EqualsAndHashCode.Include
	public abstract int getPrice();
}
