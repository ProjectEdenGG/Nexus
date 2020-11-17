package me.pugabyte.bncore.features.menus.rewardchests;

import lombok.Getter;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public enum RewardChestType {
	ALL,
	VOTE,
	MYSTERY,
	PUGMAS;

	ItemStack item = new ItemBuilder(Material.TRIPWIRE_HOOK).name("&eMystery Chest Key")
			.lore(" ").lore("&3Type: &e" + StringUtils.camelCase(name()))
			.lore("&7Use me on the Mystery Chest").lore("&7at spawn to receive a reward").build();
}
