package me.pugabyte.nexus.features.minigames.mechanics;

import me.pugabyte.nexus.features.minigames.mechanics.common.DeathmatchMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class TeamDeathmatch extends DeathmatchMechanic {

	@Override
	public String getName() {
		return "Team Deathmatch";
	}

	@Override
	public String getDescription() {
		return "Kill the other team";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.LEATHER_HELMET);
	}

}
