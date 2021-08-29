package gg.projecteden.nexus.features.events.y2020.bearfair20.quests;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20.isAtBearFair;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20.isInRegion;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20.send;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests.bottomBlockError;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests.cantBreakError;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests.decorOnlyError;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests.itemLore;
import static gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests.notFullyGrownError;

public class RegenCrops implements Listener {

	private List<Material> breakList = Arrays.asList(Material.WHEAT, Material.POTATOES, Material.CARROTS,
			Material.BEETROOTS, Material.MELON, Material.PUMPKIN, Material.SUGAR_CANE, Material.COCOA);
	private List<Material> noAge = Collections.singletonList(Material.SUGAR_CANE);
	//
	private static Map<Location, Material> multiRegenMap = new HashMap<>();
	private static Map<Location, Material> blockRegenMap = new HashMap<>();
	private static List<Location> cropRegenList = new ArrayList<>();

	public RegenCrops() {
		Nexus.registerListener(this);
		regenTasks();
	}

	public static void shutdown() {
		List<Location> locationsList = new ArrayList<>(cropRegenList);
		for (Location loc : locationsList) {
			Block block = loc.getBlock();
			BlockData blockData = block.getBlockData();

			if (!(blockData instanceof Ageable ageable)) {
				cropRegenList.remove(loc);
				continue;
			}

			int age = ageable.getAge();
			if (age == ageable.getMaximumAge()) {
				cropRegenList.remove(loc);
				continue;
			}

			ageable.setAge(ageable.getMaximumAge());
			block.setBlockData(ageable);
			cropRegenList.remove(loc);
		}
		//
		Set<Location> locationsSet = new HashSet<>(blockRegenMap.keySet());
		for (Location loc : locationsSet) {
			Block block = loc.getBlock();
			Material material = blockRegenMap.get(loc);
			if (block.getType().equals(material)) {
				blockRegenMap.remove(loc);
				continue;
			}

			block.setType(material);
			blockRegenMap.remove(loc);
		}
		//
		locationsSet = new HashSet<>(multiRegenMap.keySet());
		for (Location loc : locationsSet) {
			Block block = loc.getBlock();
			Material material = multiRegenMap.get(loc);
			if (material == null) {
				multiRegenMap.remove(loc);
				continue;
			}

			if (block.getType().equals(material)) {
				multiRegenMap.remove(loc);
				continue;
			}

			Block down = block.getRelative(0, -1, 0);
			if (down.getType().equals(material)) {
				block.setType(material);
				multiRegenMap.remove(loc);
			}

		}
	}

	private void regenTasks() {
		// CROPS
		Tasks.repeat(0, TickTime.SECOND.x(5), () -> {
			List<Location> locations = new ArrayList<>(cropRegenList);
			for (Location loc : locations) {
				Block block = loc.getBlock();
				BlockData blockData = block.getBlockData();

				if (!(blockData instanceof Ageable)) {
					cropRegenList.remove(loc);
					continue;
				}

				if (RandomUtils.chanceOf(20)) {
					Ageable ageable = (Ageable) blockData;
					int age = ageable.getAge();
					if (age == ageable.getMaximumAge()) {
						cropRegenList.remove(loc);
						continue;
					}
					++age;
					ageable.setAge(age);
					block.setBlockData(ageable);

					if (age == ageable.getMaximumAge()) {
						cropRegenList.remove(loc);
					}
				}
			}
		});

		// BLOCKS
		Tasks.repeat(0, TickTime.SECOND.x(10), () -> {
			Set<Location> locations = new HashSet<>(blockRegenMap.keySet());
			for (Location loc : locations) {
				Block block = loc.getBlock();
				Material material = blockRegenMap.get(loc);
				if (block.getType().equals(material)) {
					blockRegenMap.remove(loc);
					continue;
				}

				if (RandomUtils.chanceOf(20)) {
					block.setType(material);
					blockRegenMap.remove(loc);
				}
			}
		});

		// MULTIBLOCK
		Tasks.repeat(0, TickTime.SECOND.x(10), () -> {
			Set<Location> locations = new HashSet<>(multiRegenMap.keySet());
			for (Location loc : locations) {
				Block block = loc.getBlock();
				Material material = multiRegenMap.get(loc);
				if (material == null) {
					multiRegenMap.remove(loc);
					continue;
				}

				if (block.getType().equals(material)) {
					multiRegenMap.remove(loc);
					continue;
				}

				if (RandomUtils.chanceOf(20)) {
					Block down = block.getRelative(0, -1, 0);
					if (down.getType().equals(material)) {
						block.setType(material);
						multiRegenMap.remove(loc);
					}
				}
			}
		});
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (event.isCancelled()) return;
		if (!isAtBearFair(block)) return;
		if (isInRegion(block, Quarry.quarryRg)) return;
		if (!breakList.contains(block.getType())) {
			if (player.hasPermission("worldguard.region.bypass.*")) return;
			send(cantBreakError, player);
			event.setCancelled(true);
			return;
		}

		if (player.hasPermission("worldguard.region.bypass.*")) {
			if (player.getInventory().getItemInMainHand().getType().equals(Material.NETHER_BRICK))
				return;
		}

		BlockData blockData = block.getState().getBlockData();
		Material material = block.getType();
		if (!(blockData instanceof Ageable ageable) || noAge.contains(material)) {
			switch (material) {
				case MELON, PUMPKIN -> {
					if (!(block.getRelative(0, -1, 0).getType().equals(Material.COARSE_DIRT))) {
						send(decorOnlyError, player);
						event.setCancelled(true);
						return;
					}
					Tasks.wait(20, () -> blockRegenMap.put(block.getLocation(), material));
				}
				case SUGAR_CANE -> {
					if (!(block.getRelative(0, -1, 0).getType().equals(material))) {
						send(bottomBlockError, player);
						event.setCancelled(true);
						return;
					}
					multiRegenMap.put(block.getLocation(), material);
					Block above = block.getRelative(0, 1, 0);
					if (above.getType().equals(material)) {
						int yvalue = above.getLocation().getBlockY();
						for (int i = yvalue; i < 255; i++) {
							if (above.getType().equals(material)) {
								Location aboveLoc = above.getLocation();
								above.setType(Material.AIR, false);
								above.getWorld().dropItemNaturally(aboveLoc, new ItemBuilder(material).lore(itemLore).build());
								multiRegenMap.put(aboveLoc, material);
							} else {
								break;
							}
							above = above.getRelative(0, 1, 0);
						}
					}
				}
				default -> {
					if (player.hasPermission("worldguard.region.bypass.*")) return;
					send(cantBreakError, player);
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10F, 1F);
					event.setCancelled(true);
				}
			}
			return;
		}

		if (ageable.getAge() != ageable.getMaximumAge()) {
			if (new CooldownService().check(player, "BF_notFullyGrown", TickTime.MINUTE)) {
				send(notFullyGrownError, player);
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10F, 1F);
			}
			event.setCancelled(true);
			return;
		}

		Tasks.wait(5, () -> {
			ageable.setAge(0);
			block.setType(material);
			block.setBlockData(ageable);
			Location loc = block.getLocation();
			cropRegenList.add(loc);
		});
	}
}
