package me.pugabyte.nexus.features.minigames.mechanics;

import me.pugabyte.nexus.features.minigames.mechanics.common.DeathmatchMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class FourTeamDeathmatch extends DeathmatchMechanic {

	@Override
	public String getName() {
		return "Four Team Deathmatch";
	}

	@Override
	public String getDescription() {
		return "Kill the other teams";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.DIAMOND_SWORD);
	}

}
