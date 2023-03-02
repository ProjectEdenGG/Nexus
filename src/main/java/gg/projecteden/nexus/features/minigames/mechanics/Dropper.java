package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Dropper extends TeamlessMechanic {

	@Override
	public @NotNull String getName() {
		return "Dropper";
	}

	@Override
	public @NotNull String getDescription() {
		return "Reach the bottom without dying";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.DROPPER);
	}

	@EventHandler
	public void on(PlayerEnteringRegionEvent event) {
		final Player player = event.getPlayer();
		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		final Match match = minigamer.getMatch();
		if (!event.getRegion().equals(match.getArena().getProtectedRegion("win")))
			return;

		// TODO Points algo
		minigamer.scored(1);

		match.getTimer().setTime(30);

		minigamer.teleportAsync(Objects.requireNonNull(match.getArena().getSpectateLocation()));
	}

}
