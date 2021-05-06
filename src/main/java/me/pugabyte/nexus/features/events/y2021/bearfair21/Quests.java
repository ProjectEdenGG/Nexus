package me.pugabyte.nexus.features.events.y2021.bearfair21;

import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.Fishing;
import me.pugabyte.nexus.features.recipes.functionals.Backpacks;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Quests {
	public Quests() {
		new Fishing();
	}

	public static ItemStack getBackPack(Player player) {
		return Backpacks.getBackpack(null, player);
	}
}
