package me.pugabyte.bncore.features.minigames.mechanics;

import io.papermc.lib.PaperLib;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
			map.put((minigamer.isAlive() ? "" : "&c&m") + minigamer.getName(), (minigamer.isAlive() ? (int) minigamer.getPlayer().getHealth() : 0));
		}
		return map;
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);
		WorldBorder border = Bukkit.getWorld(world).getWorldBorder();
		border.reset();
	}

	private void spreadPlayers(List<Minigamer> minigamers) {
		Location center = Bukkit.getWorld(world).getHighestBlockAt(Utils.randomInt(-5000, 5000), Utils.randomInt(-5000, 5000)).getLocation();
		for (Minigamer minigamer : minigamers) {
			int tries = 0;
			Location loc;
			do {
				loc = Bukkit.getWorld(world).getHighestBlockAt(Utils.randomInt(-gameRadius / 2, gameRadius / 2),
						Utils.randomInt(-gameRadius / 2, gameRadius / 2)).getLocation().add(new Vector(center.getX(), 0, center.getZ()));
				tries++;
			} while (!loc.getBlock().getType().isSolid() && tries < 20);
			Location newLoc = loc.clone();
			if (!PaperLib.isChunkGenerated(newLoc))
				PaperLib.getChunkAtAsync(newLoc, true).thenAccept(chunk -> minigamer.teleport(newLoc.clone().add(0, 2, 0)));
			else
				minigamer.teleport(newLoc.clone().add(0, 2, 0));
		}
		WorldBorder border = Bukkit.getWorld(world).getWorldBorder();
		border.setCenter(center);
		border.setSize(gameRadius);
		border.setDamageAmount(0);
		border.setWarningDistance(1);
	}

	public void swap(Match match) {
		List<Minigamer> swappingList = new ArrayList(match.getMinigamers().stream().filter(Minigamer::isAlive).collect(Collectors.toList()));
		swappingList.forEach(player -> {
			player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
			Utils.sendActionBar(player.getPlayer(), "&3SWAPPING");
		});

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
		match.getMinigamers().stream().filter(Minigamer::isAlive).collect(Collectors.toList()).forEach(player -> {
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
