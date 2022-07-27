package gg.projecteden.nexus.features.minigames.mechanics;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Maze extends Parkour {

	@Override
	public @NotNull String getName() {
		return "Maze";
	}

	@Override
	public @NotNull String getDescription() {
		return "Navigate your way to the end of these twisting tunnels";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.OAK_LEAVES);
	}

}
