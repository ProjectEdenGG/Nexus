package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class FreeForAll extends TeamlessMechanic {

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
	public void onDeath(MinigamerDeathEvent event) {
		super.onDeath(event);
		if (event.getAttacker() != null)
			event.getAttacker().scored();
	}

}
