package me.pugabyte.nexus.models.achievement;

import eden.utils.EnumUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum AchievementGroup {
	COMBAT(new ItemStack(Material.DIAMOND_SWORD)),
	SOCIAL(new ItemStack(Material.WRITABLE_BOOK)),
	ECONOMY(new ItemStack(Material.GOLD_INGOT)),
	TRAVEL(new ItemStack(Material.ENDER_PEARL)),
	BIOMES(new ItemStack(Material.GRASS)),
	MISC(new ItemStack(Material.POTATO)),

	BEAR_FAIR(new ItemStack(Material.PAINTING));

	@Getter
	private final ItemStack itemStack;

	AchievementGroup(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public String toString() {
		return EnumUtils.prettyName(name());
	}
}
