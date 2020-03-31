package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.models.annotations.Railgun;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Railgun
public class Quake extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Quake";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.IRON_HOE);
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		if (event.getAttacker() != null)
			event.getAttacker().scored();
		super.onDeath(event);
	}

}
