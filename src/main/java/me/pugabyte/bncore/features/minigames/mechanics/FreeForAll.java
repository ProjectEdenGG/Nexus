package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FreeForAll extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Free For All";
	}

	@Override
	public String getDescription() {
		return "Kill everyone!";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.IRON_SWORD);
	}

	@Override
	public void onDeath(Minigamer victim, Minigamer killer) {
		super.onDeath(victim, killer);
		killer.scored();
	}

}
