package gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.frogger;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.EventSounds;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand.canWorldGuardEdit;
import static gg.projecteden.nexus.features.vanish.Vanish.isVanished;

public class Pugmas25Frogger implements Listener {

	@Getter
	private static final String PREFIX = "&8&l[&eFrogger&8&l] &f";
	private static final Pugmas25 PUGMAS = Pugmas25.get();

	private static final String GAME_REGION = PUGMAS.getRegionName() + "_frogger";

	private static final String BASE_REGION = GAME_REGION + "_";
	private static final String WIN_REGION = BASE_REGION + "win";
	private static final String DAMAGE_REGION = BASE_REGION + "damage";
	private static final String KILL_REGION = BASE_REGION + "kill";
	private static final String LOGS_REGION = BASE_REGION + "logs";
	private static final String CARS_REGION_1 = BASE_REGION + "cars_1";
	private static final String ROAD_REGION_1 = BASE_REGION + "road_1";
	private static final String CARS_REGION_2 = BASE_REGION + "cars_2";
	private static final String ROAD_REGION_2 = BASE_REGION + "road_2";
	private static final String CHECKPOINT_REGION = BASE_REGION + "checkpoint";
	//
	private static final Location RESPAWN_LOC = PUGMAS.location(-787.5, 78.0, -2858.5, 90, 0);
	private static final Location CHECKPOINT_LOC = PUGMAS.location(-807.5, 78.0, -2858.5, 90, 0);
	private static final Set<Player> checkpointList = new HashSet<>();
	private static boolean enabled = false;
	private static int animationTaskId;
	//
	private static final Map<Location, Material> logSpawnMap = new HashMap<>();
	private static final List<Integer> logTasks = new ArrayList<>();
	private static final Material logMaterial = Material.SPRUCE_WOOD;
	private static final Material riverMaterial = Material.WATER;
	//
	private static final Map<Location, Material> carSpawnMap = new HashMap<>();
	private static final List<Integer> carTasks = new ArrayList<>();
	private static final Set<Material> carMaterials = MaterialTag.CONCRETES.exclude(Material.BLACK_CONCRETE, Material.LIGHT_GRAY_CONCRETE).getValues();

	public Pugmas25Frogger() {
		WorldGuardUtils worldguard = PUGMAS.worldguard();

		worldguard.getRegion(GAME_REGION);
		worldguard.getRegion(WIN_REGION);
		worldguard.getRegion(DAMAGE_REGION);
		worldguard.getRegion(KILL_REGION);
		worldguard.getRegion(LOGS_REGION);
		worldguard.getRegion(CARS_REGION_1);
		worldguard.getRegion(ROAD_REGION_1);
		worldguard.getRegion(CARS_REGION_2);
		worldguard.getRegion(ROAD_REGION_2);
		worldguard.getRegion(CHECKPOINT_REGION);
		worldguard.getProtectedRegion(GAME_REGION);
		Nexus.registerListener(this);
	}

	private void loadLogSpawns() {
		loadSpawns(LOGS_REGION, logSpawnMap);
	}

	private void loadCarSpawns() {
		loadSpawns(CARS_REGION_1, carSpawnMap);
		loadSpawns(CARS_REGION_2, carSpawnMap);
	}

	private void loadSpawns(String regionId, Map<Location, Material> spawnMap) {
		List<Block> blocks = PUGMAS.worldedit().getBlocks(PUGMAS.worldguard().getRegion(regionId));
		for (Block block : blocks)
			if (block.getType().equals(Material.DIAMOND_BLOCK) || block.getType().equals(Material.EMERALD_BLOCK))
				spawnMap.put(block.getLocation(), block.getType());
	}

	public void startAnimations() {
		// Log Animations
		AtomicInteger taskId = new AtomicInteger();
		taskId.set(Tasks.wait(0, () -> {
			Pugmas25FroggerUtils.clearLogs(LOGS_REGION, logMaterial, riverMaterial);
			int lastLogLen = 3;

			if (logSpawnMap.isEmpty())
				loadLogSpawns();

			for (Location spawnLoc : logSpawnMap.keySet()) {
				BlockFace blockFace = (spawnLoc.getBlock().getType().equals(Material.DIAMOND_BLOCK)) ? BlockFace.NORTH : BlockFace.SOUTH;
				for (int i = 0; i < 4; i++) {

					int ran = RandomUtils.randomInt(2, 3);
					int wait = ((lastLogLen * 10) + 30) + (((RandomUtils.randomInt(1, 3)) * 10) * i);
					lastLogLen = ran;

					Tasks.wait((long) wait * i, () -> {
						if (animationTaskId == taskId.get())
							logTask(ran, spawnLoc, blockFace);
					});
				}
			}

			// Car Animations
			Pugmas25FroggerUtils.clearCars(ROAD_REGION_1);
			Pugmas25FroggerUtils.clearCars(ROAD_REGION_2);

			if (carSpawnMap.isEmpty())
				loadCarSpawns();

			for (Location spawnLoc : carSpawnMap.keySet()) {
				Location loc = spawnLoc.getBlock().getRelative(0, 2, 0).getLocation();
				BlockFace blockFace = (spawnLoc.getBlock().getType().equals(Material.DIAMOND_BLOCK)) ? BlockFace.NORTH : BlockFace.SOUTH;

				int wait = RandomUtils.randomInt(0, 14);
				Tasks.wait(wait, () -> {
					if (animationTaskId == taskId.get())
						carTask(loc, blockFace);
				});

				wait += 28;
				Tasks.wait(wait + RandomUtils.randomInt(0, 14), () -> {
					if (animationTaskId == taskId.get())
						carTask(loc, blockFace);
				});

				wait += 28;
				Tasks.wait(wait + RandomUtils.randomInt(0, 14), () -> {
					if (animationTaskId == taskId.get())
						carTask(loc, blockFace);
				});
			}
		}));

		animationTaskId = taskId.get();
	}

	public static void stopAnimations() {
		for (Integer logTask : logTasks)
			Tasks.cancel(logTask);
		logTasks.clear();

		for (Integer carTask : carTasks)
			Tasks.cancel(carTask);
		carTasks.clear();

		checkpointList.clear();
	}

	private void logTask(int maxLength, Location location, BlockFace blockFace) {
		final Location start = location.clone().getBlock().getRelative(blockFace).getLocation();
		AtomicReference<Location> current = new AtomicReference<>(start.clone());
		AtomicInteger distance = new AtomicInteger(0);
		AtomicInteger currentLength = new AtomicInteger(0);

		int taskId = Tasks.repeat(0, 10, () -> {
			if (!enabled) {
				stopAnimations();
				return;
			}

			// If the next block is bedrock
			Block next = current.get().clone().getBlock().getRelative(blockFace);
			if (next.getType().equals(Material.BEDROCK)) {
				Block behind = current.get().clone().getBlock().getRelative(blockFace.getOppositeFace(), currentLength.get());
				behind.setType(riverMaterial);
				currentLength.decrementAndGet();

				// if currentLength < 0, LOOP
				if (currentLength.get() < 0) {
					current.set(location.clone());
					distance.set(0);
				}
			}
			// If block at next location is not bedrock, set it to log
			else {
				current.set(start.clone().getBlock().getRelative(blockFace, distance.get()).getLocation());
				current.get().getBlock().setType(logMaterial);
				distance.incrementAndGet();
				currentLength.incrementAndGet();

				// if currentLen >= maxLen, set the block maxLen blocks behind currentLoc to AIR
				if (currentLength.get() > maxLength) {
					Block block = current.get().clone().getBlock().getRelative(blockFace.getOppositeFace(), currentLength.get());
					if (!block.getType().equals(Material.DIAMOND_BLOCK) && !block.getType().equals(Material.EMERALD_BLOCK))
						block.setType(riverMaterial);
					currentLength.decrementAndGet();
				}
			}
		});

		logTasks.add(taskId);
	}

	private void carTask(Location location, BlockFace blockFace) {
		int maxLen = 3;
		final Location startLoc = location.clone();
		AtomicReference<Location> currentLoc = new AtomicReference<>(startLoc.clone());
		AtomicReference<Material> carMaterial = new AtomicReference<>(RandomUtils.randomElement(carMaterials));
		AtomicInteger distance = new AtomicInteger(0);
		AtomicInteger currentLen = new AtomicInteger(0);

		int taskId = Tasks.repeat(0, 2, () -> {
			if (!enabled) {
				stopAnimations();
				return;
			}

			// If the next block is black stained glass
			Block next = currentLoc.get().clone().getBlock().getRelative(blockFace);
			if (next.getType().equals(Material.BLACK_STAINED_GLASS)) {
				Block behind = currentLoc.get().clone().getBlock().getRelative(blockFace.getOppositeFace(), currentLen.get());
				Pugmas25FroggerUtils.removeCarSlice(behind);
				currentLen.decrementAndGet();

				// if currentLen < 0, LOOP
				if (currentLen.get() < 0) {
					currentLoc.set(location.clone());
					distance.set(0);
					carMaterial.set(RandomUtils.randomElement(carMaterials));
				}
			}
			// If block at next location is not bedrock, set it to car
			else {
				currentLoc.set(startLoc.clone().getBlock().getRelative(blockFace, distance.get()).getLocation());
				Pugmas25FroggerUtils.buildCar(currentLoc.get().clone(), blockFace, carMaterial.get(), currentLen.get());
				distance.incrementAndGet();
				currentLen.incrementAndGet();

				// if currentLen >= maxLen, set the block maxLen blocks behind currentLoc to AIR
				if (currentLen.get() > maxLen) {
					Block behind = currentLoc.get().clone().getBlock().getRelative(blockFace.getOppositeFace(), currentLen.get());
					if (!behind.getType().equals(Material.BLACK_STAINED_GLASS))
						Pugmas25FroggerUtils.removeCarSlice(behind);
					currentLen.decrementAndGet();
				}
			}
		});

		carTasks.add(taskId);

	}

	//

	@EventHandler
	public void onRegionEnter(PlayerEnteredRegionEvent event) {
		String regionId = event.getRegion().getId();
		Player player = event.getPlayer();
		if (regionId.equalsIgnoreCase(GAME_REGION)) {
			if (enabled)
				return;
			enabled = true;
			startAnimations();

		} else if (regionId.equalsIgnoreCase(CHECKPOINT_REGION)) {
			checkpointList.add(player);

		} else if (regionId.equalsIgnoreCase(DAMAGE_REGION)) {
			String cheatingMsg = isCheatingMsg(player);
			if (cheatingMsg != null && !cheatingMsg.contains("wgedit")) {
				player.teleportAsync(RESPAWN_LOC);
				PUGMAS.sendNoPrefix(player, Pugmas25Frogger.getPREFIX() + "Don't cheat, turn " + cheatingMsg + " off!");
				EventSounds.VILLAGER_NO.play(player);
			}

		} else if (regionId.equalsIgnoreCase(KILL_REGION)) {
			if (canWorldGuardEdit(player)) return;
			if (checkpointList.contains(player))
				player.teleportAsync(CHECKPOINT_LOC);
			else
				player.teleportAsync(RESPAWN_LOC);
			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BIT).receiver(player).volume(10).play();

		} else if (regionId.equalsIgnoreCase(WIN_REGION)) {
			if (canWorldGuardEdit(player)) return;

			checkpointList.remove(player);
			player.teleportAsync(RESPAWN_LOC);
			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BIT).receiver(player).volume(10).pitch(2.0).play();

			// TODO: GIVE DAILY TOKENS
		}
	}

	@EventHandler
	public void onRegionExit(PlayerLeftRegionEvent event) {
		String regionId = event.getRegion().getId();
		if (regionId.equalsIgnoreCase(GAME_REGION)) {
			int size = PUGMAS.worldguard().getPlayersInRegion(GAME_REGION).size();
			if (size == 0) {
				enabled = false;
				stopAnimations();
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		if (!PUGMAS.isInRegion(player, DAMAGE_REGION)) return;
		if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;

		event.setDamage(0);
		event.setCancelled(true);
		if (checkpointList.contains(player))
			player.teleportAsync(CHECKPOINT_LOC);
		else
			player.teleportAsync(RESPAWN_LOC);
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BIT).receiver(player).volume(10).play();
	}

	private static String isCheatingMsg(Player player) {
		if (canWorldGuardEdit(player)) return "wgedit";
		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return "creative";
		if (player.isFlying()) return "fly";
		if (isVanished(player)) return "vanish";
		if (new GodmodeService().get(player).isActive()) return "godmode";

		return null;
	}

}
