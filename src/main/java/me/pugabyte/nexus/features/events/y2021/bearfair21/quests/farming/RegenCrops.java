package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.farming;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.Errors;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.isAtBearFair;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.send;

public class RegenCrops implements Listener {

	private final Set<Material> breakList = new HashSet<>();

	private final Set<Material> crops = new HashSet<>(Arrays.asList(Material.WHEAT, Material.POTATOES, Material.CARROTS, Material.BEETROOTS));
	private final Set<Material> cropSingleBlock = new HashSet<>(Arrays.asList(Material.PUMPKIN, Material.MELON));
	private final Set<Material> cropMultiBlock = new HashSet<>(Arrays.asList(Material.SUGAR_CANE, Material.CACTUS));
	private final Set<Material> cropFlower = new HashSet<>(MaterialTag.SMALL_FLOWERS.getValues());
	//
	private static final Map<Location, Material> multiRegenMap = new HashMap<>();
	private static final Map<Location, Material> blockRegenMap = new HashMap<>();
	private static final List<Location> cropRegenList = new ArrayList<>();

	public RegenCrops() {
		Nexus.registerListener(this);
		breakList.addAll(crops);
		breakList.addAll(cropSingleBlock);
		breakList.addAll(cropMultiBlock);
		breakList.addAll(cropFlower);
		regenTasks();
	}

	public static void shutdown() {
		List<Location> locationsList = new ArrayList<>(cropRegenList);
		for (Location loc : locationsList) {
			Block block = loc.getBlock();
			BlockData blockData = block.getBlockData();

			if (!(blockData instanceof Ageable)) {
				cropRegenList.remove(loc);
				continue;
			}

			Ageable ageable = (Ageable) blockData;
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
		Tasks.repeat(0, Time.SECOND.x(5), () -> {
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
		Tasks.repeat(0, Time.SECOND.x(10), () -> {
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
		Tasks.repeat(0, Time.SECOND.x(10), () -> {
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
		if (!breakList.contains(block.getType())) {
			if (player.hasPermission("worldguard.region.bypass.*"))
				return;

			send(Errors.cantBreak, player);
			event.setCancelled(true);
			return;
		}

		if (player.hasPermission("worldguard.region.bypass.*")) {
			if (player.getInventory().getItemInMainHand().getType().equals(Material.NETHER_BRICK))
				return;
		}

		BlockData blockData = block.getState().getBlockData();
		Material material = block.getType();
		if (!(blockData instanceof Ageable) || cropMultiBlock.contains(material)) {

			// Flower
			if (cropFlower.contains(material)) {
				Tasks.wait(20, () -> blockRegenMap.put(block.getLocation(), RandomUtils.randomElement(cropFlower)));

				// Single Block
			} else if (cropSingleBlock.contains(material)) {
				if (!(block.getRelative(0, -1, 0).getType().equals(Material.COARSE_DIRT))) {
					send(Errors.decorOnly, player);
					event.setCancelled(true);
					return;
				}
				Tasks.wait(20, () -> blockRegenMap.put(block.getLocation(), material));

				// Multi Block
			} else if (cropMultiBlock.contains(material)) {
				if (!(block.getRelative(0, -1, 0).getType().equals(material))) {
					send(Errors.bottomBlock, player);
					event.setCancelled(true);
					return;
				}

				multiRegenMap.put(block.getLocation(), material);
				Block above = block.getRelative(0, 1, 0);
				if (above.getType().equals(material)) {
					int yValue = above.getLocation().getBlockY();
					for (int i = yValue; i < 255; i++) {
						if (!above.getType().equals(material))
							break;

						Location aboveLoc = above.getLocation();
						above.setType(Material.AIR, false);
						above.getWorld().dropItemNaturally(aboveLoc, new ItemBuilder(material).build());
						multiRegenMap.put(aboveLoc, material);
						above = above.getRelative(0, 1, 0);
					}
				}
			} else {
				if (player.hasPermission("worldguard.region.bypass.*"))
					return;

				send(Errors.cantBreak, player);
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10F, 1F);
				event.setCancelled(true);
			}

			return;
		}

		Ageable ageable = (Ageable) blockData;
		if (ageable.getAge() != ageable.getMaximumAge()) {
			if (new CooldownService().check(player, "BF21_notFullyGrown", Time.MINUTE)) {
				send(Errors.notFullyGrown, player);
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
