package gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.frogger;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.EventSounds;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
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

// TODO SETUP: MAKE SURE TO DISABLE SITTING IN GAME REGION
public class Frogger implements Listener {
	private static final String gameRg = Pugmas24.get().getRegionName() + "_frogger";
	private static final String winRg = gameRg + "_win";
	private static final String damageRg = gameRg + "_damage";
	private static final String killRg = gameRg + "_kill";
	private static final String logsRg = gameRg + "_logs";
	private static final String carsRg1 = gameRg + "_cars_1";
	private static final String roadRg1 = gameRg + "_road_1";
	private static final String carsRg2 = gameRg + "_cars_2";
	private static final String roadRg2 = gameRg + "_road_2";
	private static final String checkpointRg = gameRg + "_checkpoint";
	//
	private static final Location respawnLoc = Pugmas24.get().location(-520.5, 250.0, -2719.5, 90, 0);
	private static final Location checkpointLoc = Pugmas24.get().location(-540.5, 250.0, -2719.5, 90, 0);
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

	public Frogger() {
		WorldGuardUtils worldguard = Pugmas24.get().worldguard();

		worldguard.getRegion(gameRg);
		worldguard.getRegion(winRg);
		worldguard.getRegion(damageRg);
		worldguard.getRegion(killRg);
		worldguard.getRegion(logsRg);
		worldguard.getRegion(carsRg1);
		worldguard.getRegion(roadRg1);
		worldguard.getRegion(carsRg2);
		worldguard.getRegion(roadRg2);
		worldguard.getRegion(checkpointRg);
		worldguard.getProtectedRegion(gameRg);
		Nexus.registerListener(this);
	}

	private void loadLogSpawns() {
		loadSpawns(logsRg, logSpawnMap);
	}

	private void loadCarSpawns() {
		loadSpawns(carsRg1, carSpawnMap);
		loadSpawns(carsRg2, carSpawnMap);
	}

	private void loadSpawns(String regionId, Map<Location, Material> spawnMap) {
		List<Block> blocks = Pugmas24.get().worldedit().getBlocks(Pugmas24.get().worldguard().getRegion(regionId));
		for (Block block : blocks)
			if (block.getType().equals(Material.DIAMOND_BLOCK) || block.getType().equals(Material.EMERALD_BLOCK))
				spawnMap.put(block.getLocation(), block.getType());
	}

	public void startAnimations() {
		// Log Animations
		AtomicInteger taskId = new AtomicInteger();
		taskId.set(Tasks.wait(0, () -> {
			FroggerUtils.clearLogs(logsRg, logMaterial, riverMaterial);
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
			FroggerUtils.clearCars(roadRg1);
			FroggerUtils.clearCars(roadRg2);

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
				FroggerUtils.removeCarSlice(behind);
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
				FroggerUtils.buildCar(currentLoc.get().clone(), blockFace, carMaterial.get(), currentLen.get());
				distance.incrementAndGet();
				currentLen.incrementAndGet();

				// if currentLen >= maxLen, set the block maxLen blocks behind currentLoc to AIR
				if (currentLen.get() > maxLen) {
					Block behind = currentLoc.get().clone().getBlock().getRelative(blockFace.getOppositeFace(), currentLen.get());
					if (!behind.getType().equals(Material.BLACK_STAINED_GLASS))
						FroggerUtils.removeCarSlice(behind);
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
		if (regionId.equalsIgnoreCase(gameRg)) {
			if (enabled)
				return;
			enabled = true;
			startAnimations();

		} else if (regionId.equalsIgnoreCase(checkpointRg)) {
			checkpointList.add(player);

		} else if (regionId.equalsIgnoreCase(damageRg)) {
			String cheatingMsg = Pugmas24.isCheatingMsg(player);
			if (cheatingMsg != null && !cheatingMsg.contains("wgedit")) {
				player.teleportAsync(respawnLoc);
				Pugmas24.send("Don't cheat, turn " + cheatingMsg + " off!", player);
				EventSounds.VILLAGER_NO.play(player);
			}

		} else if (regionId.equalsIgnoreCase(killRg)) {
			if (canWorldGuardEdit(player)) return;
			if (checkpointList.contains(player))
				player.teleportAsync(checkpointLoc);
			else
				player.teleportAsync(respawnLoc);
			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BIT).receiver(player).volume(10).play();

		} else if (regionId.equalsIgnoreCase(winRg)) {
			if (canWorldGuardEdit(player)) return;

			checkpointList.remove(player);
			player.teleportAsync(respawnLoc);
			new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BIT).receiver(player).volume(10).pitch(2.0).play();

			//BearFair21.giveDailyTokens(player, BF21PointSource.FROGGER, 5);
		}
	}

	@EventHandler
	public void onRegionExit(PlayerLeftRegionEvent event) {
		String regionId = event.getRegion().getId();
		if (regionId.equalsIgnoreCase(gameRg)) {
			int size = Pugmas24.get().worldguard().getPlayersInRegion(gameRg).size();
			if (size == 0) {
				enabled = false;
				stopAnimations();
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		if (!Pugmas24.get().isInRegion(player, damageRg)) return;
		if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;

		event.setDamage(0);
		event.setCancelled(true);
		if (checkpointList.contains(player))
			player.teleportAsync(checkpointLoc);
		else
			player.teleportAsync(respawnLoc);
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BIT).receiver(player).volume(10).play();
	}

}
