package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class Megamind extends TeamlessMechanic {

	@Override
	public @NotNull String getName() {
		return "Megamind";
	}

	@Override
	public @NotNull String getDescription() {
		return "TODO";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.TRIPWIRE_HOOK);
	}

	@Override
	public boolean isTestMode() {
		return true;
	}

	@Override
	public boolean useScoreboardNumbers(Match match) {
		return false;
	}
}
