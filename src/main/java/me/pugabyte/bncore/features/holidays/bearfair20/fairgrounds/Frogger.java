package me.pugabyte.bncore.features.holidays.bearfair20.fairgrounds;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;

// TODO - Animation
public class Frogger implements Listener {

	private static WorldEditUtils WEUtils = new WorldEditUtils(BearFair20.world);
	private static String gameRg = BearFair20.mainRg + "_frogger";
	private static String winRg = gameRg + "_win";
	private static String damageRg = gameRg + "_damage";
	private static String logsRg = gameRg + "_logs";
	private static Location respawnLoc = new Location(BearFair20.world, -856.5, 138, -1623.5, -180, 0);
	private static boolean doAnimation = false;
	private static Map<Location, Material> logSpawnMap = new HashMap<>();
	private static List<Integer> logTasks = new ArrayList<>();
	private static Material logMaterial = Material.SPRUCE_WOOD;
	private static Material riverMaterial = Material.LAVA;
//	private static List<Integer> carTasks  = new ArrayList<>();

	public Frogger() {
		BNCore.registerListener(this);
		loadLogSpawns();
	}

	private void loadLogSpawns() {
		List<Block> blocks = WEUtils.getBlocks((CuboidRegion) WGUtils.getRegion(logsRg));
		for (Block block : blocks) {
			if (block.getType().equals(Material.DIAMOND_BLOCK) || block.getType().equals(Material.EMERALD_BLOCK)) {
				logSpawnMap.put(block.getLocation(), block.getType());
			}
		}
	}

	public void startAnimations() {
		clearLogs();
		Set<Location> spawnLocs = logSpawnMap.keySet();
		int lastLogLen = 3;
		for (Location spawnLoc : spawnLocs) {
			BlockFace blockFace = (spawnLoc.getBlock().getType().equals(Material.DIAMOND_BLOCK)) ? BlockFace.WEST : BlockFace.EAST;
//			int interval = Utils.randomInt(5, 10);
			for (int i = 0; i < 4; i++) {

				// log length 1-4
				int ran = Utils.randomInt(2, 3);
				// wait = log length + 2-3
				// wait = 80 is a good standard if loglen = 4
				int wait = (((20 * lastLogLen) + (Utils.randomInt(2, 4) * 10) + 10) * i);
				lastLogLen = ran;

				Tasks.wait(wait * i, () -> {
					logTask(ran, spawnLoc, blockFace);
				});
			}
		}
	}

	public void stopAnimations() {
		for (Integer logTask : logTasks) {
			Tasks.cancel(logTask);
		}
//		for (Integer carTask : carTasks) {
//			Tasks.cancel(carTask);
//		}
	}

	private void logTask(int maxLength, Location location, BlockFace blockFace) {
		final Location start = location.clone().getBlock().getRelative(blockFace).getLocation();
		AtomicReference<Location> current = new AtomicReference<>(start.clone());
		AtomicInteger distance = new AtomicInteger(0);
		AtomicInteger currentLength = new AtomicInteger(0);
//		AtomicInteger currentGap = new AtomicInteger(0);
//		int gap = 3;

		int taskId = Tasks.repeat(0, 10, () -> {
			if (!doAnimation)
				stopAnimations();

//			Utils.wakka("Current Len: " + currentLength.get() + " || " + maxLength);
//			Utils.wakka("Current Loc: " + current.get().getBlockX() + " " + current.get().getBlockY() + " " + current.get().getBlockZ());
//			Utils.wakka("Distance: " + distance.get());

			// If the next block is bedrock
			Block next = current.get().clone().getBlock().getRelative(blockFace);
//			Utils.wakka("Next Block: " + next.getType());
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

//			Utils.wakka("");
		});

		logTasks.add(taskId);
	}

	private void clearLogs() {
		List<Block> blocks = WEUtils.getBlocks((CuboidRegion) WGUtils.getRegion(logsRg));
		for (Block block : blocks) {
			if (block.getType().equals(logMaterial))
				block.setType(riverMaterial);
		}
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		String regionId = event.getRegion().getId();
		Player player = event.getPlayer();
		if (regionId.equalsIgnoreCase(gameRg)) {
			if (doAnimation)
				return;
			doAnimation = true;
			startAnimations();
		} else if (regionId.equalsIgnoreCase(damageRg)) {
			String cheatingMsg = BearFair20.isCheatingMsg(player);
			if (cheatingMsg != null && !cheatingMsg.contains("wgedit")) {
				player.teleport(respawnLoc);
				player.sendMessage("Don't cheat, turn " + cheatingMsg + " off!");
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10F, 1F);
			}
		} else if (regionId.equalsIgnoreCase(winRg)) {
			player.teleport(respawnLoc);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10F, 2F);
			BearFair20.givePoints(player, 1);

		}
	}

	@EventHandler
	public void onRegionExit(RegionLeftEvent event) {
		String regionId = event.getRegion().getId();
		if (regionId.equalsIgnoreCase(gameRg)) {
			int size = WGUtils.getPlayersInRegion(gameRg).size();
			if (size == 0) {
				doAnimation = false;
				stopAnimations();
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;

		Player player = (Player) event.getEntity();
		if (!WGUtils.isInRegion(player.getLocation(), damageRg)) return;

		if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;
		event.setDamage(0);
		player.setFireTicks(0);
		player.teleport(respawnLoc);
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 10F, 1F);

	}

}
