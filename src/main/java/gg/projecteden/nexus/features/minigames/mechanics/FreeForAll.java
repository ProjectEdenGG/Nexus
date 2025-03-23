package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.models.RegenType;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchStatisticsClass;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.minigames.models.statistics.models.generics.PVPStats;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@MatchStatisticsClass(PVPStats.class)
public final class FreeForAll extends TeamlessMechanic {

	@Override
	public @NotNull String getName() {
		return "Free For All";
	}

	@Override
	public @NotNull String getDescription() {
		return "Kill all your enemy opponents";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.IRON_SWORD);
	}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		if (event.getAttacker() != null)
			event.getAttacker().scored();
		super.onDeath(event);
	}

	@Override
	public RegenType getRegenType() {
		return RegenType.TIER_4;
	}
}
