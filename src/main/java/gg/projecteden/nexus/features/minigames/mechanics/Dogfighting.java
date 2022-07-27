package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.models.annotations.Railgun;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Railgun(cooldownTicks = 0, mustBeGliding = true)
public class Dogfighting extends Quake {

	@Override
	public @NotNull String getName() {
		return "Dogfighting";
	}

	@Override
	public @NotNull String getDescription() {
		return "Take to the skies and shoot your opponents with a railgun in this aerial war";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.ELYTRA);
	}

}
