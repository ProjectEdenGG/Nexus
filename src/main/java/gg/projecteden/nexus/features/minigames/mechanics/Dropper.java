package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.arenas.DropperArena;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
		match.broadcast(minigamer.getNickname() + " reached the bottom");

		if (match.getTimer().getTime() != 0)
			match.getTimer().setTime(30);

		if (match.getArena().getSpectateLocation() != null)
			minigamer.teleportAsync(match.getArena().getSpectateLocation());
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);

		DropperArena arena = event.getMatch().getArena();
		for (Minigamer minigamer : event.getMatch().getMinigamers())
			minigamer.teleportAsync(RandomUtils.randomElement(arena.getCurrentMap().getSpawnpoints()));
	}

	@Override
	public void onDeath(@NotNull Minigamer victim) {
		final DropperArena arena = victim.getMatch().getArena();
		victim.teleportAsync(RandomUtils.randomElement(arena.getCurrentMap().getSpawnpoints()));
	}

}
