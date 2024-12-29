package gg.projecteden.nexus.features.minigames.mechanics;

import com.destroystokyo.paper.entity.Pathfinder;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.DebugDotCommand;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.MonsterMazeMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.minigames.utils.PowerUpUtils;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/*
	TODO:
		Checkpoint platform
			Beacon
		No regen
		Take 2 hearts on launch
		Give 1 heart on reaching safe zone
		Block break animation after checkpoint
		Glass around start platform
		Invisible players w/ boots?
		Prevent mobs from pathfinding onto checkpoint platforms
 */
public class MonsterMaze extends TeamlessMechanic {
	// Arena
	private Material floorMaterial = Material.STONE;
	private Material goalMaterial = Material.GOLD_BLOCK;
	private int MONSTERS = 15;
	private int POWERUPS = 3;
	private List<EntityType> mobTypes = List.of(EntityType.ZOMBIE);

	// MatchData

	// Mechanic
	private static final String NBT_KEY = "MonsterMaze";

	@Override
	public @NotNull String getName() {
		return "Monster Maze";
	}

	@Override
	public boolean isTestMode() {
		return true;
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
		Region goalsRegion = match.getArena().getRegion("goals");
		Region floorRegion = match.getArena().getRegion("floor");

		for (BlockVector3 vector : goalsRegion) {
			Location location = match.worldguard().toLocation(vector);
			if (location.getBlock().getType() == goalMaterial)
				matchData.getGoals().add(location.add(0, 1, 0));
		}

		List<Block> spawnpoints = match.worldguard().getRandomBlocks(floorRegion, floorMaterial, MONSTERS);
		spawnpoints.stream().map(block -> block.getLocation().add(.5, 1, .5)).forEach(spawnpoint -> {
			Mob monster = (Mob) match.spawn(spawnpoint, RandomUtils.randomElement(mobTypes));
			monster.setAI(false);
			monster.setSilent(true);
			monster.setCollidable(false);
			monster.setInvulnerable(true);
			monster.setMetadata(NBT_KEY, new FixedMetadataValue(Nexus.getInstance(), true));

			if (monster instanceof Zombie zombie)
				zombie.setShouldBurnInDay(false);
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
			matchData.updatePath(mob);
		}

		match.getTasks().repeat(0, 30, () -> {
			for (Mob mob : match.getEntities(Mob.class)) {
				Pathfinder pathfinder = matchData.getPathfinder(mob);
				if (pathfinder.getCurrentPath() == null)
					matchData.updatePath(mob);
				else if (pathfinder.getCurrentPath().getFinalPoint() == null)
					matchData.updatePath(mob);
				else if (pathfinder.getCurrentPath().getFinalPoint().getBlock().getRelative(BlockFace.DOWN).getType() != Material.GOLD_BLOCK)
					matchData.updatePath(mob);
				else if (mob.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.GOLD_BLOCK)
					matchData.updatePath(mob);
			}
		});

		match.getTasks().repeat(0, 2, () -> {
			for (Minigamer minigamer : match.getMinigamers())
				for (Mob mob : match.getEntities(Mob.class))
					if (Distance.distance(mob, minigamer).lt(.7)) {
						minigamer.getOnlinePlayer().damage(4);
						matchData.launch(minigamer, mob);
					}
		});

		List<Block> powerupLocations = match.worldguard().getRandomBlocks(floor, floorMaterial, POWERUPS);
		match.broadcast("Power ups have spawned!");
		for (Block block : powerupLocations)
			new PowerUpUtils(match, Arrays.asList(JUMPS)).spawn(block.getLocation().add(0, 1, 0), true);
	}

	private void allowJump(Minigamer minigamer) {
		minigamer.getOnlinePlayer().removePotionEffect(PotionEffectType.JUMP);
	}

	private void preventJump(Minigamer minigamer) {
		minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.JUMP).infinite().amplifier(250));
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (!event.getDamager().hasMetadata(NBT_KEY))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(EntityTargetEvent event) {
		if (!event.getEntity().hasMetadata(NBT_KEY))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(EntityPathfindEvent event) {
		if (!event.getEntity().hasMetadata(NBT_KEY))
			return;

		if (Dev.GRIFFIN.isOnline())
			DebugDotCommand.play(Dev.GRIFFIN.getOnlinePlayer(), event.getLoc().toCenterLocation());

		if (event.getTargetEntity() != null)
			event.setCancelled(true);
		else if (event.getLoc().getBlock().getRelative(BlockFace.DOWN).getType() != Material.GOLD_BLOCK)
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
	// repulse



}
