package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;

public class BFQuests implements Listener {
	private ProtectedRegion mainRegion = WGUtils.getProtectedRegion(BearFair20.bearfairRg);
	private List<Material> breakList = Arrays.asList(Material.WHEAT, Material.POTATOES, Material.CARROTS,
			Material.BEETROOTS, Material.MELON, Material.PUMPKIN, Material.SUGAR_CANE);
	private List<Material> noAge = Collections.singletonList(Material.SUGAR_CANE);
	private String cantBreak = "Can't touch this";
	private List<Location> regenList = new ArrayList<>();

	public BFQuests() {
		BNCore.registerListener(this);
		regenTask();
	}

	private void regenTask() {
		Tasks.repeat(0, Time.SECOND.x(5), () -> {
			List<Location> locations = new ArrayList<>(regenList);
			for (Location loc : locations) {
				Block block = loc.getBlock();
				BlockData blockData = block.getBlockData();

				if (!(blockData instanceof Ageable)) {
					regenList.remove(loc);
					continue;
				}

				Ageable ageable = (Ageable) blockData;
				int age = ageable.getAge() + 1;
				ageable.setAge(age);
				block.setBlockData(ageable);

				if (age >= ageable.getMaximumAge()) {
					regenList.remove(loc);
				}
			}
		});
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (!WGUtils.getRegionsAt(block.getLocation()).contains(mainRegion)) return;
		if (player.hasPermission("worldguard.region.bypass.*")) return;
		if (!breakList.contains(block.getType())) {
			player.sendMessage(cantBreak);
			event.setCancelled(true);
			return;
		}

		BlockData blockData = block.getState().getBlockData();
		Material material = block.getType();
		if (!(blockData instanceof Ageable) || noAge.contains(material)) {
			switch (material) {
				case MELON:
				case PUMPKIN:
					break;
				case SUGAR_CANE:
					if (!(block.getRelative(0, -1, 0).getType().equals(Material.SUGAR_CANE))) {
						player.sendMessage("Can't break the bottom block");
						event.setCancelled(true);
						return;
					}
					break;
				default:
					player.sendMessage(cantBreak);
					event.setCancelled(true);
			}
			return;
		}

		Ageable ageable = (Ageable) blockData;
		if (ageable.getAge() != ageable.getMaximumAge()) {
			player.sendMessage("Not fully grown");
			event.setCancelled(true);
			return;
		}

		Tasks.wait(5, () -> {
			ageable.setAge(0);
			block.setType(material);
			block.setBlockData(ageable);
			Location loc = block.getLocation();
			regenList.add(loc);
		});
	}

	@EventHandler
	public void onBlockDropItemEvent(BlockDropItemEvent event) {
		Location loc = event.getBlock().getLocation();
		if (!WGUtils.getRegionsAt(loc).contains(mainRegion)) return;
		event.getItems().forEach(item -> {
			item.getItemStack().setLore(Collections.singletonList("BearFair20 Item"));
		});
	}

	@EventHandler
	public void onEvent(McMMOPlayerXpGainEvent event) {
		Location loc = event.getPlayer().getLocation();
		if (!WGUtils.getRegionsAt(loc).contains(mainRegion)) return;
		event.setRawXpGained(0F);
		event.setCancelled(true);
	}
}
