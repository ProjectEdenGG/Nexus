package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.mechanics.common.CheckpointMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Parkour extends CheckpointMechanic {

	@Override
	public String getName() {
		return "Parkour";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.FEATHER);
	}

}
