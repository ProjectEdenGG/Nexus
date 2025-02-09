package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.arenas.DropperArena;
import gg.projecteden.nexus.features.minigames.models.arenas.DropperMap;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchTimerTickEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
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

	private static void toSpawnpoint(@NotNull Minigamer minigamer) {
		DropperArena arena = minigamer.getMatch().getArena();
		minigamer.getOnlinePlayer().setFallDistance(0);
		minigamer.getOnlinePlayer().setAllowFlight(false);
		minigamer.getOnlinePlayer().setFlying(false);
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

		matchData.getFinished().clear();
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
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		event.showDeathMessage(false);
		toSpawnpoint(event.getMinigamer());
	}

	@Override
	public void onDamage(@NotNull MinigamerDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onWin(PlayerEnteringRegionEvent event) {
		final Player player = event.getPlayer();
		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		final Match match = minigamer.getMatch();
		if (!(match.getArena() instanceof DropperArena arena))
			return;

		if (!event.getRegion().getId().equals(match.getArena().getProtectedRegion("win").getId()))
			return;

		final DropperMatchData matchData = match.getMatchData();
		if (matchData.getFinished().contains(minigamer))
			return;

		matchData.getFinished().add(minigamer);

		int size = matchData.getFinished().size();
		minigamer.scored(Math.max(1, 1 + (4 - size)));

		match.broadcast(minigamer.getNickname() + " reached the bottom");

		if (matchData.getFinished().size() == match.getAliveMinigamers().size())
			match.getTimer().setTime(6);
		else if (match.getTimer().getTime() > 31)
			match.getTimer().setTime(31);

		minigamer.getOnlinePlayer().setAllowFlight(true);
		minigamer.getOnlinePlayer().setFlying(true);

		if (arena.getCurrentMap().getSpectateLocation() != null)
			minigamer.teleportAsync(arena.getCurrentMap().getSpectateLocation());
	}

}
