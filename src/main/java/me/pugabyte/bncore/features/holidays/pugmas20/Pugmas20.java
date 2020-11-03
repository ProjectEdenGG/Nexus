package me.pugabyte.bncore.features.holidays.pugmas20;

import fr.minuskube.inv.content.SlotPos;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class Pugmas20 implements Listener {
	@Getter
	public static final String region = "pugmas20";
	@Getter
	public static final World world = Bukkit.getWorld("buildadmin"); // TODO Pugmas - correct world
	@Getter
	public static final String PREFIX = StringUtils.getPrefix("Pugmas20");
	public WorldGuardUtils utils = new WorldGuardUtils(world);
	// Advent Menu
	private static final Location adventHeadsLoc = new Location(Pugmas20.world, -956, 9, -2096);
	@Getter
	public static LinkedHashMap<SlotPos, ItemBuilder> adventHeadMap = new LinkedHashMap<>();

	public Pugmas20() {
		BNCore.registerListener(this);

		loadAdventMenuHeads();
	}

	private void loadAdventMenuHeads() {
		Block origin = adventHeadsLoc.getBlock().getRelative(BlockFace.UP);
		String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturaday"};

		int day = 1;
		for (int z = 0; z <= 4; z++) {        // 1-7 row
			for (int x = 1; x <= 7; x++) {    // 0-4 col
				Block block = origin.getRelative(x, 0, z);
				if (!Utils.isNullOrAir(block)) {
					ItemStack drop = block.getDrops().stream().findFirst().orElse(null);
					if (!Utils.isNullOrAir(drop)) {
						ItemBuilder skull = new ItemBuilder(drop);
						int size = adventHeadMap.size();
						if (size <= 6)
							skull.name(days[size]);
						else
							skull.name("Day: " + day++);

						adventHeadMap.put(new SlotPos(z, x), skull);
					}
				}
			}
		}
	}
}
