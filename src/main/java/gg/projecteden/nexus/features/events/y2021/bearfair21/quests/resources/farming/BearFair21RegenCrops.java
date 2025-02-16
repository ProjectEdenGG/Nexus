package gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.farming;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BearFair21RegenCrops {
	@Getter
	private static final Map<Location, Material> multiRegenMap = new HashMap<>();
	@Getter
	private static final Map<Location, Material> blockRegenMap = new HashMap<>();
	@Getter
	private static final List<Location> cropRegenList = new ArrayList<>();

	public BearFair21RegenCrops() {
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

}
