package gg.projecteden.nexus.features.minigames.mechanics;

import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.MonsterMazeMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.minigames.utils.PowerUpUtils;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.utils.Distance.distance;

public class MonsterMaze extends TeamlessMechanic {
	// Arena
	private Material floorMaterial = Material.STONE;
	private Material goalMaterial = Material.GOLD_BLOCK;
	private int MONSTERS = 10;
	private int POWERUPS = 3;

	// MatchData

	// Mechanic
	private static final String NBT_KEY = "MonsterMaze";

	@Override
	public @NotNull String getName() {
		return "Monster Maze";
	}

	@Override
	public @NotNull String getDescription() {
		return "Get to the beacon without touching the monsters";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.SPAWNER);
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);

		Match match = event.getMatch();
		MonsterMazeMatchData matchData = match.getMatchData();
		Region floor = match.getArena().getRegion("floor");

		for (BlockVector3 vector : floor) {
			Location location = match.worldguard().toLocation(vector);
			if (location.getBlock().getType() == goalMaterial)
				matchData.getGoals().add(location.add(0, 1, 0));
		}

		List<Block> spawnpoints = match.worldguard().getRandomBlocks(floor, floorMaterial, MONSTERS);
		spawnpoints.stream().map(block -> block.getLocation().add(.5, 1, .5)).forEach(spawnpoint -> {
			Mob monster = match.spawn(spawnpoint, Zombie.class);
			monster.setAI(false);
			monster.setSilent(true);
			monster.setCollidable(false);
			monster.setInvulnerable(true);
			monster.setMetadata(NBT_KEY, new FixedMetadataValue(Nexus.getInstance(), true));
		});

		match.getMinigamers().forEach(this::preventJump);
	}

	@Override
	public void onBegin(@NotNull MatchBeginEvent event) {
		Match match = event.getMatch();
		MonsterMazeMatchData matchData = match.getMatchData();
		Region floor = match.getArena().getRegion("floor");

		for (Mob mob : match.getEntities(Mob.class)) {
			mob.setAI(true);
			updatePath(mob, matchData.getGoals());
		}

		match.getTasks().repeat(TickTime.SECOND.x(7), 30, () -> {
			for (Mob mob : match.getEntities(Mob.class))
				if (!mob.getPathfinder().hasPath())
					updatePath(mob, matchData.getGoals());
		});

		match.getTasks().repeat(0, 2, () -> {
			for (Minigamer minigamer : match.getMinigamers())
				for (Mob mob : match.getEntities(Mob.class))
					if (distance(mob, minigamer).lt(.7)) {
						minigamer.getOnlinePlayer().damage(4);
						launch(minigamer, mob);
					}
		});

		List<Block> powerupLocations = match.worldguard().getRandomBlocks(floor, floorMaterial, POWERUPS);
		match.broadcast("Power ups have spawned!");
		for (Block block : powerupLocations)
			new PowerUpUtils(match, Arrays.asList(JUMPS)).spawn(block.getLocation().add(0, 1, 0), true);
	}

	private void launch(Minigamer minigamer, Mob monster) {
		Location playerCenterLocation = minigamer.getOnlinePlayer().getEyeLocation();
		Location playerToThrowLocation = monster.getEyeLocation();

		double x = playerCenterLocation.getX() - playerToThrowLocation.getX();
		double y = playerCenterLocation.getY() - playerToThrowLocation.getY();
		double z = playerCenterLocation.getZ() - playerToThrowLocation.getZ();

		Vector throwVector = new Vector(x, y, z).normalize().multiply(1.1).setY(1.3);

		minigamer.getOnlinePlayer().setVelocity(throwVector);
	}

	private void updatePath(Mob monster, List<Location> goals) {
		monster.getPathfinder().moveTo(getNewGoal(monster.getLocation(), goals));
	}

	public Location getNewGoal(Location start, List<Location> goals) {
		HashMap<Location, Double> distances = new HashMap<>();
		goals.forEach(goal -> distances.put(goal, distance(start, goal).get()));

		List<Location> sorted = distances.entrySet().stream()
			.sorted(Map.Entry.comparingByValue())
			.map(Map.Entry::getKey)
			.toList();

		return sorted.get(RandomUtils.randomInt(sorted.size() / 2, sorted.size() - 1));
	}

	private void allowJump(Minigamer minigamer) {
		minigamer.getOnlinePlayer().removePotionEffect(PotionEffectType.JUMP);
	}

	private void preventJump(Minigamer minigamer) {
		minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.JUMP).maxDuration().amplifier(250));
	}

	@EventHandler
	public void on(EntityPathfindEvent event) {
		if (event.getEntity().hasMetadata(NBT_KEY))
			if (event.getTargetEntity() != null)
				event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerJump(PlayerJumpEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		PlayerInventory inventory = event.getPlayer().getInventory();
		ItemStack item = inventory.getItem(8);
		if (item == null) {
			Nexus.warn("[MonsterMaze] " + minigamer.getNickname() + " was allowed to jump without powerup");
			preventJump(minigamer);
			return;
		}

		if (item.getType() != Material.FEATHER) {
			Nexus.warn("[MonsterMaze] " + minigamer.getNickname() + " was allowed to jump without powerup (Material is " + item.getType() + ")");
			preventJump(minigamer);
			return;
		}

		item.setAmount(item.getAmount() - 1);
		if (item.getAmount() == 0) {
			minigamer.tell("You have used all your jumps!");
			preventJump(minigamer);
			return;
		}

		inventory.setItem(8, item);
	}

	private final PowerUpUtils.PowerUp JUMPS = new PowerUpUtils.PowerUp("3 Jumps", true, Material.FEATHER, minigamer -> {
		minigamer.getOnlinePlayer().getInventory().setItem(8, new ItemStack(Material.FEATHER, 3));
		allowJump(minigamer);
	});

	// healing
	// slowness snowballs
	// shoo zombies

}
