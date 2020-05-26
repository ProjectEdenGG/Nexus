package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class DeathSwap extends TeamlessMechanic {
	@Override
	public String getName() {
		return "Death Swap";
	}

	@Override
	public String getDescription() {
		return "Trap players by swapping with them!";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.ENDER_PEARL);
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	public int gameRadius = 1000;
	public String world = "deathswap";

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);

		event.getMatch().getTasks().wait(Time.SECOND.x(1), () -> event.getMatch().getMinigamers());

		event.getMatch().getTasks().wait(Time.SECOND.x(10), () -> delay(event.getMatch()));
	}

	private void spreadPlayers(List<Minigamer> minigamers) {
		for (Minigamer minigamer : minigamers) {
			Location loc = Bukkit.getWorld(world).getHighestBlockAt(Utils.randomInt(-gameRadius / 2, gameRadius / 2),
					Utils.randomInt(-gameRadius / 2, gameRadius / 2)).getLocation();
			for (Minigamer _minigamer : minigamer.getMatch().getMinigamers()) {
				if (_minigamer.getPlayer().getLocation().distance(loc) < 50)
					spreadPlayers(minigamers);
			}
			minigamer.teleport(loc.add(0, 2, 0));
			minigamers.remove(minigamer);
		}
	}

	public void swap(Match match) {
		match.getMinigamers().forEach(player -> Utils.sendActionBar(player.getPlayer(), "&3SWAPPING"));
		ArrayList<Minigamer> swappingList = new ArrayList<>(match.getMinigamers());
		if (match.getMinigamers().size() % 2 != 0) {
			Minigamer playerOne = Utils.getRandomElement(swappingList);
			swappingList.remove(playerOne);
			Minigamer playerTwo = Utils.getRandomElement(swappingList);
			swappingList.remove(playerTwo);
			Minigamer playerThree = Utils.getRandomElement(swappingList);
			swappingList.remove(playerThree);
			Location one = playerOne.getPlayer().getLocation();
			Location two = playerTwo.getPlayer().getLocation();
			Location three = playerThree.getPlayer().getLocation();
			playerOne.teleport(three);
			playerTwo.teleport(one);
			playerThree.teleport(two);
		}
		while (swappingList.size() > 0) {
			Minigamer playerOne = Utils.getRandomElement(swappingList);
			swappingList.remove(playerOne);
			Minigamer playerTwo = Utils.getRandomElement(swappingList);
			swappingList.remove(playerTwo);
			Location one = playerOne.getPlayer().getLocation();
			Location two = playerTwo.getPlayer().getLocation();
			playerOne.teleport(two);
			playerTwo.teleport(one);
		}
		delay(match);
	}

	public void delay(Match match) {
		if (match.getMinigamers().size() <= 1) {
			match.end();
			return;
		}
		match.getTasks().wait(Time.SECOND.x(Utils.randomInt(5, 10)), () -> swap(match));
	}

}
