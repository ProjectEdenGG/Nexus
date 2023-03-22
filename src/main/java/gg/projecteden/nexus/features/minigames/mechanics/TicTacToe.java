package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class TicTacToe extends TeamMechanic {

	@Override
	public @NotNull String getName() {
		return "TicTacToe";
	}

	@Override
	public @NotNull String getDescription() {
		return "TODO";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.WHITE_CONCRETE);
	}

	@Override
	public boolean isTestMode() {
		return true;
	}

}
