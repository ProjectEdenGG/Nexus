package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class ElytraRacing extends TeamlessMechanic {

	@Override
	public @NotNull String getName() {
		return "Elytra Racing";
	}

	@Override
	public @NotNull String getDescription() {
		return "TODO";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.ELYTRA);
	}

	@Override
	public boolean isTestMode() {
		return true;
	}

}
