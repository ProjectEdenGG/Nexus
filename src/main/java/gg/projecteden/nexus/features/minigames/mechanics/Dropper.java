package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.arenas.DropperArena;
import gg.projecteden.nexus.features.minigames.models.arenas.DropperMap;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchTimerTickEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.DropperMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Dropper extends TeamlessMechanic {
	private static final int ROUNDS = 5;

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

	@Override
	public boolean isTestMode() {
		return true;
	}

	private static void toSpawnpoint(@NotNull Minigamer minigamer) {
		DropperArena arena = minigamer.getMatch().getArena();
		minigamer.teleportAsync(RandomUtils.randomElement(arena.getCurrentMap().getSpawnpoints()));
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		nextTurn(event.getMatch());
	}

	@Override
	public void nextTurn(@NotNull Match match) {
		super.nextTurn(match);

		final DropperMatchData matchData = match.getMatchData();

		if (matchData.getRound() == ROUNDS)
			match.broadcast("Final round");
		else if (matchData.getRound() != 1)
			match.broadcast("Next round");

		for (Minigamer minigamer : match.getMinigamers())
			toSpawnpoint(minigamer);
	}

	@EventHandler
	public void on(MatchTimerTickEvent event) {
		if (event.getTime() != 1)
			return;

		final Match match = event.getMatch();
		if (!(match.getArena() instanceof DropperArena arena))
			return;

		final DropperMatchData matchData = match.getMatchData();

		matchData.getPlayedMaps().add(arena.getCurrentMap());

		if (matchData.getPlayedMaps().size() >= ROUNDS)
			return;

		final List<DropperMap> unplayedMaps = new ArrayList<>(arena.getMaps()) {{
			removeAll(matchData.getPlayedMaps());
		}};

		final DropperMap nextMap = RandomUtils.randomElement(unplayedMaps);

		if (arena.getCurrentMap() == null)
			return;

		arena.setCurrentMap(nextMap);
		match.getTimer().setTime(arena.getSeconds());

		nextTurn(match);
	}

	@Override
	public void onDeath(@NotNull Minigamer victim) {
		toSpawnpoint(victim);
	}

	@EventHandler
	public void onWin(PlayerEnteringRegionEvent event) {
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

		if (match.getTimer().getTime() > 31)
			match.getTimer().setTime(31);

		if (match.getArena().getSpectateLocation() != null)
			minigamer.teleportAsync(match.getArena().getSpectateLocation());
	}

}
