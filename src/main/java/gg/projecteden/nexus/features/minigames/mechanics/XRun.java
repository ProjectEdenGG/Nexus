package gg.projecteden.nexus.features.minigames.mechanics;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class XRun extends Parkour {

	@Override
	public @NotNull String getName() {
		return "X-Run";
	}

	@Override
	public @NotNull String getDescription() {
		return "Race your way to the finish line";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.SUGAR);
	}

}
