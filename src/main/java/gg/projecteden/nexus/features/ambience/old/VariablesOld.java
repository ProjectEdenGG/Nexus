package gg.projecteden.nexus.features.ambience.old;

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
public class VariablesOld {
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
		Location location = player.getLocation();
		int playerX = location.getBlockX() + 1;
		int playerY = location.getBlockY() + 1;
		int playerZ = location.getBlockZ() + 1;

		World world = player.getWorld();
		for (int x = playerX - 2; x <= playerX; x++) {
			for (int y = playerY - 2; y <= playerY; y++) {
				for (int z = playerZ - 2; z <= playerZ; z++) {
					if (!world.getBlockAt(x, y, z).getType().equals(Material.AIR))
						continue;
					if (world.getBlockAt(x, y, z).getLightFromSky() > 4) // increased from 0
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
