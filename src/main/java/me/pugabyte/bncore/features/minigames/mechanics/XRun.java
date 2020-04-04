package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.mechanics.common.CheckpointMechanic;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class XRun extends CheckpointMechanic {

	@Override
	public String getName() {
		return "X-Run";
	}

	@Override
	public String getDescription() {
		return "Race your way to the finish line";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.SUGAR);
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		event.setDeathMessage(null);
		super.onDeath(event);
	}

}
