package me.pugabyte.nexus.features.minigames.mechanics;

import me.pugabyte.nexus.features.minigames.models.annotations.Railgun;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Railgun(cooldownTicks = 0)
public class Dogfighting extends Quake {

	@Override
	public @NotNull String getName() {
		return "Dogfighting";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.ELYTRA);
	}

}
