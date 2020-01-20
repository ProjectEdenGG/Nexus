package me.pugabyte.bncore.features.minigames.mechanics;

import com.github.ysl3000.bukkit.pathfinding.PathfinderGoalAPI;
import com.github.ysl3000.bukkit.pathfinding.entity.Insentient;
import com.github.ysl3000.bukkit.pathfinding.goals.PathfinderGoalMoveToLocation;
import com.github.ysl3000.bukkit.pathfinding.pathfinding.PathfinderManager;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.matchdata.MonsterMazeMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.framework.exceptions.BNException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MonsterMaze extends TeamlessMechanic {
	private Material goalMaterial = Material.GOLD_BLOCK;
	private Material floorMaterial = Material.STONE;

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
	public void onInitialize(Match match) {
		super.onInitialize(match);
		match.setMatchData(new MonsterMazeMatchData(match));
	}

	@Override
	public void onStart(Match match) {
		super.onStart(match);
		MonsterMazeMatchData matchData = (MonsterMazeMatchData) match.getMatchData();
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

		int ZOMBIES = 10;
		List<Location> spawnpoints = new ArrayList<>();
		while (spawnpoints.size() < ZOMBIES) {
			Block spawnpoint = Minigames.getWorldGuardUtils().getRandomBlock(floor, floorMaterial);
			if (spawnpoint == null) continue;
			spawnpoints.add(spawnpoint.getLocation().add(0, 1, 0));
		}

		spawnpoints.forEach(spawnpoint -> {
			Zombie zombie = (Zombie) spawnpoint.getWorld().spawnEntity(spawnpoint, EntityType.ZOMBIE);
			zombie.setSilent(true);
			zombie.setInvulnerable(true);
			zombie.setCollidable(false);
			matchData.getZombies().add(zombie);
		});

		match.getTasks().wait(5 * 20, () -> {
			for (Zombie zombie : matchData.getZombies())
				updatePath(zombie, goals);

			match.getTasks().repeat(7 * 20, 10, () -> {
				for (Zombie zombie : matchData.getZombies()) {
					Insentient insentient = pathApi.getPathfinderGoalEntity(zombie);
					if (insentient.getNavigation().isDoneNavigating())
						updatePath(zombie, goals);
				}
			});
		});
	}

	private void updatePath(Zombie zombie, List<Location> goals) {
		PathfinderManager pathApi = PathfinderGoalAPI.INSTANCE.getAPI();
		Location goal = getFarthestGoal(zombie.getLocation(), goals);
		Insentient insentient = pathApi.getPathfinderGoalEntity(zombie);
		insentient.clearPathfinderGoals();
		PathfinderGoalMoveToLocation path = new PathfinderGoalMoveToLocation(insentient, goal, 1, 1);
		insentient.addPathfinderGoal(0, path);
	}

	public Location getFarthestGoal(Location start, List<Location> goals) {
		Location farthest = goals.get(0);
		double distance = 0;
		for (Location goal : goals) {
			double newDistance = start.distance(goal);
			if (newDistance > distance) {
				distance = newDistance;
				farthest = goal;
			}
		}
		return farthest;
	}

	@Override
	public void onEnd(Match match) {
		super.onEnd(match);
		MonsterMazeMatchData matchData = (MonsterMazeMatchData) match.getMatchData();
		matchData.getZombies().forEach(Entity::remove);
	}

	@Override
	protected void onDamage(Minigamer victim, EntityDamageEvent event) {
		super.onDamage(victim, event);
	}

}
