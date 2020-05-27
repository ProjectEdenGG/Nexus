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
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Override
	public boolean canOpenInventoryBlocks() {
		return true;
	}

	public int gameRadius = 1000;
	public String world = "deathswap";

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);

		event.getMatch().getMinigamers().forEach(minigamer -> minigamer.setScore(20));
		event.getMatch().getTasks().wait(Time.SECOND.x(1), () -> spreadPlayers(new ArrayList(event.getMatch().getMinigamers())));

		event.getMatch().getTasks().wait(Time.SECOND.x(30), () -> delay(event.getMatch()));
	}

	@Override
	public Map<String, Integer> getScoreboardLines(Match match) {
		Map<String, Integer> map = new HashMap<>();
		for (Minigamer minigamer : match.getMinigamers()) {
			map.put(minigamer.getColoredName(), (int) minigamer.getPlayer().getHealth());
		}
		return map;
	}

	private void spreadPlayers(List<Minigamer> minigamers) {
		for (Minigamer minigamer : minigamers) {
			Location loc = Bukkit.getWorld(world).getHighestBlockAt(Utils.randomInt(-gameRadius / 2, gameRadius / 2),
					Utils.randomInt(-gameRadius / 2, gameRadius / 2)).getLocation();
			minigamer.teleport(loc.add(0, 2, 0));
		}
	}

	public void swap(Match match) {
		match.getMinigamers().forEach(player -> {
			player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
			Utils.sendActionBar(player.getPlayer(), "&3SWAPPING");
		});
		List<Minigamer> swappingList = new ArrayList(match.getMinigamers());
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
		aggroMobs(match);
		delay(match);
	}

	public void aggroMobs(Match match) {
		match.getMinigamers().forEach(player -> {
			Utils.getNearbyEntities(player.getPlayer().getLocation(), 10).keySet().forEach(entity -> {
				if (!(entity instanceof Mob)) return;
				Mob mob = (Mob) entity;
				if (mob.getTarget() instanceof Player)
					((Mob) entity).setTarget(player.getPlayer());
			});
		});
	}

	public void delay(Match match) {
		if (match.getMinigamers().size() <= 1) {
			match.end();
			return;
		}
		match.getTasks().wait(Time.SECOND.x(Utils.randomInt(30, 120)), () -> swap(match));
	}

}
