package gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.farming;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.bearfair21.Quests;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.Errors;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21.send;

public class Farming implements Listener {
	private static final Set<Material> breakList = new HashSet<>();

	private static final Set<Material> crops = new HashSet<>(Arrays.asList(Material.WHEAT, Material.POTATOES,
			Material.CARROTS, Material.BEETROOTS, Material.COCOA));
	private static final Set<Material> cropSingleBlock = new HashSet<>(Arrays.asList(Material.PUMPKIN, Material.MELON));
	private static final Set<Material> cropMultiBlock = new HashSet<>(Arrays.asList(Material.SUGAR_CANE, Material.CACTUS));
	private static final Set<Material> cropFlower = new HashSet<>(MaterialTag.SMALL_FLOWERS.getValues());

	public Farming() {
		Nexus.registerListener(this);
		breakList.addAll(crops);
		breakList.addAll(cropSingleBlock);
		breakList.addAll(cropMultiBlock);
		breakList.addAll(cropFlower);
		new RegenCrops();
	}

	public static boolean breakBlock(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (!breakList.contains(block.getType()))
			return false;

		BlockData blockData = block.getState().getBlockData();
		Material material = block.getType();

		if (!(blockData instanceof Ageable ageable) || cropMultiBlock.contains(material)) {

			// Flower
			if (cropFlower.contains(material)) {
				Tasks.wait(20, () -> RegenCrops.getBlockRegenMap().put(block.getLocation(), RandomUtils.randomElement(cropFlower)));

				// Single Block
			} else if (cropSingleBlock.contains(material)) {
				if (!(block.getRelative(0, -1, 0).getType().equals(Material.COARSE_DIRT))) {
					if (new CooldownService().check(player, "BF21_decorOnly", TickTime.MINUTE)) {
						send(Errors.DECOR_ONLY, player);
						Quests.sound_villagerNo(player);
					}
					return true;
				}
				Quests.giveExp(player);
				Tasks.wait(20, () -> RegenCrops.getBlockRegenMap().put(block.getLocation(), material));

				// Multi Block
			} else if (cropMultiBlock.contains(material)) {
				if (!(block.getRelative(0, -1, 0).getType().equals(material))) {
					if (new CooldownService().check(player, "BF21_bottomBlock", TickTime.MINUTE)) {
						send(Errors.BOTTOM_BLOCK, player);
						Quests.sound_villagerNo(player);
					}
					return true;
				}

				Quests.giveExp(player);
				RegenCrops.getMultiRegenMap().put(block.getLocation(), material);
				Block above = block.getRelative(0, 1, 0);
				if (above.getType().equals(material)) {
					int yValue = above.getLocation().getBlockY();
					for (int i = yValue; i < 255; i++) {
						if (!above.getType().equals(material))
							break;

						Location aboveLoc = above.getLocation();
						above.setType(Material.AIR, false);
						above.getWorld().dropItemNaturally(aboveLoc, new ItemBuilder(material).build());
						RegenCrops.getMultiRegenMap().put(aboveLoc, material);
						above = above.getRelative(0, 1, 0);
					}
				}
			}

			event.setCancelled(false);
			return true;
		}

		if (ageable.getAge() != ageable.getMaximumAge()) {
			if (new CooldownService().check(player, "BF21_notFullyGrown", TickTime.MINUTE)) {
				send(Errors.NOT_FULLY_GROWN, player);
				Quests.sound_villagerNo(player);
			}
			return true;
		}

		Quests.giveExp(player);
		Tasks.wait(5, () -> {
			ageable.setAge(0);
			block.setType(material);
			block.setBlockData(ageable);
			Location loc = block.getLocation();
			RegenCrops.getCropRegenList().add(loc);
		});

		event.setCancelled(false);
		return true;

	}
}
