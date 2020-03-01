package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.models.annotations.Railgun;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.UnbalancedTeamMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Railgun(damageWithConsole = true)
public class Murder extends UnbalancedTeamMechanic {

	@Override
	public String getName() {
		return "Murder";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.IRON_SWORD);
	}

}
