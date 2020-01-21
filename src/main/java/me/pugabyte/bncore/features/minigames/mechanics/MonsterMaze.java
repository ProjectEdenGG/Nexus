package me.pugabyte.bncore.features.minigames.mechanics;

import com.github.ysl3000.bukkit.pathfinding.PathfinderGoalAPI;
import com.github.ysl3000.bukkit.pathfinding.entity.Insentient;
import com.github.ysl3000.bukkit.pathfinding.goals.PathfinderGoalMoveToLocation;
import com.github.ysl3000.bukkit.pathfinding.pathfinding.PathfinderManager;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.matchdata.MonsterMazeMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MonsterMaze extends TeamlessMechanic {
	// Arena
	private Material floorMaterial = Material.STONE;
	private Material goalMaterial = Material.GOLD_BLOCK;
	private int ZOMBIES = 10;

	// MatchData

	@Override
	public String getName() {
		return "Monster Maze";
	}

	@Override
	public String getDescription() {
		return "Get to the next beacon";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.ZOMBIE.ordinal());
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);

		Match match = event.getMatch();
		MonsterMazeMatchData matchData = match.getMatchData();
		ProtectedRegion floor = match.getArena().getProtectedRegion("floor");

		PathfinderManager pathApi = PathfinderGoalAPI.INSTANCE.getAPI();
		if (pathApi == null)
			throw new BNException("PathfinderAPI is null?");

		List<Location> goals = new ArrayList<>();
		for (Vector vector : match.getArena().getRegion("floor")) {
			Location location = Minigames.getWorldGuardUtils().toLocation(vector);
			if (location.getBlock().getType() == goalMaterial)
				goals.add(location.add(0, 1, 0));
		}

		List<Block> spawnpoints = Minigames.getWorldGuardUtils().getRandomBlocks(floor, floorMaterial, ZOMBIES);
		spawnpoints.stream().map(block -> block.getLocation().add(.5, 1, .5)).forEach(spawnpoint -> {
			Zombie zombie = spawnpoint.getWorld().spawn(spawnpoint, Zombie.class);
			zombie.setAI(false);
			zombie.setSilent(true);
			zombie.setInvulnerable(true);
			zombie.setCollidable(false);
			matchData.getZombies().add(zombie);
		});

		match.getMinigamers().forEach(this::preventJump);

		match.getTasks().wait(5 * 20, () -> {
			for (Zombie zombie : matchData.getZombies())
				updatePath(zombie, goals);

			match.getTasks().repeat(7 * 20, 30, () -> {
				for (Zombie zombie : matchData.getZombies()) {
					Insentient insentient = pathApi.getPathfinderGoalEntity(zombie);
					if (insentient.getNavigation().isDoneNavigating())
						updatePath(zombie, goals);
				}
			});
		});
	}

	private void updatePath(Zombie zombie, List<Location> goals) {
		zombie.setAI(true);
		PathfinderManager pathApi = PathfinderGoalAPI.INSTANCE.getAPI();
		Location goal = getNewGoal(zombie.getLocation(), goals);
		Insentient insentient = pathApi.getPathfinderGoalEntity(zombie);
		insentient.clearPathfinderGoals();
		PathfinderGoalMoveToLocation path = new PathfinderGoalMoveToLocation(insentient, goal, 1, 1);
		insentient.addPathfinderGoal(0, path);
	}

	public Location getNewGoal(Location start, List<Location> goals) {
		HashMap<Location, Double> distances = new HashMap<>();
		goals.forEach(goal -> distances.put(goal, start.distance(goal)));

		List<Location> sorted = distances.entrySet().stream()
				.sorted(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		return sorted.get(Utils.randomInt(sorted.size() / 2, sorted.size() - 1));
	}

	private void allowJump(Minigamer minigamer) {
		minigamer.getPlayer().removePotionEffect(PotionEffectType.JUMP);
	}

	private void preventJump(Minigamer minigamer) {
		minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 9999999, 250));
	}

	@EventHandler
	public void onPlayerJump(PlayerStatisticIncrementEvent event) {
		if (event.getStatistic() != Statistic.JUMP) return;
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		event.getPlayer().sendMessage("Jumped");
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);
		MonsterMazeMatchData matchData = event.getMatch().getMatchData();
		matchData.getZombies().forEach(Entity::remove);
	}

	@Override
	protected void onDamage(Minigamer victim, EntityDamageEvent event) {
		super.onDamage(victim, event);
	}

}
