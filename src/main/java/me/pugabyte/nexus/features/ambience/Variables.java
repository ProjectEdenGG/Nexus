package me.pugabyte.nexus.features.ambience;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Data
public class Variables {
	Player player;
	Location location;
	Biome biome;
	//
	boolean exposed;
	boolean submerged;
	//
	long time;
	TimeQuadrant timeQuadrant;
	boolean raining;
	boolean thundering;

	public Variables(Player player) {
		this.player = player;
	}

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
		if (player == null || !player.isOnline())
			return;

		location = player.getLocation();
		biome = location.getBlock().getBiome();
		//
		exposed = exposedCheck(player);
		submerged = submergedCheck(player);
		//
		World world = player.getWorld();
		time = world.getTime();
		timeQuadrant = TimeQuadrant.of(time);
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

	@AllArgsConstructor
	public enum TimeQuadrant {
		MORNING(0, 2000),
		DAY(2000, 12000),
		EVENING(12000, 14000),
		NIGHT(14000, 24000),
		;

		private final int minTime;
		private final int maxTime;

		public static TimeQuadrant of(long time) {
			for (TimeQuadrant quadrant : values()) {
				if (time >= quadrant.minTime && time < quadrant.maxTime)
					return quadrant;
			}

			return null;
		}
	}
}
