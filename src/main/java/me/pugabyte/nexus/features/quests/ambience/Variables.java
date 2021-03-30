package me.pugabyte.nexus.features.quests.ambience;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Data
public class Variables {
	Player player = null;
	Location location;
	//
	boolean exposed;
	boolean submerged;
	//
	long dayTime;
	boolean raining;
	boolean thundering;

	public int getX() {
		return location.getBlockX();
	}

	public int getY() {
		return location.getBlockY();
	}

	public int getZ() {
		return location.getBlockZ();
	}

	public void update() {
		location = player.getLocation();
		//
		exposed = exposedCheck(player);
		submerged = submergedCheck(player);
		//
		World world = player.getWorld();
		dayTime = world.getTime() % 24000;
		raining = world.hasStorm();
		thundering = world.isThundering();
	}

	private static boolean exposedCheck(Player player) {
		Location loc = player.getLocation();
		int mx = loc.getBlockX() + 1;
		int my = loc.getBlockY() + 1;
		int mz = loc.getBlockZ() + 1;

		World world = player.getWorld();
		for (int cx = mx - 2; cx <= mx; cx++) {
			for (int cy = my - 2; cy <= my; cy++) {
				for (int cz = mz - 2; cz <= mz; cz++) {
					if (!world.getBlockAt(cx, cy, cz).getType().equals(Material.AIR))
						continue;
					if (world.getBlockAt(cx, cy, cz).getLightFromSky() > 4) // increased from 0
						return true;
				}
			}
		}
		return false;
	}

	private static boolean submergedCheck(Player player) {
		Block headBlock = player.getEyeLocation().getBlock();

		Material type = headBlock.getType();
		List<Material> included = Arrays.asList(Material.WATER, Material.BUBBLE_COLUMN, Material.KELP, Material.KELP_PLANT,
				Material.SEAGRASS, Material.TALL_SEAGRASS);
		if (included.contains(type))
			return true;

		return headBlock.getBlockData() instanceof Waterlogged && ((Waterlogged) headBlock.getBlockData()).isWaterlogged();
	}

}
