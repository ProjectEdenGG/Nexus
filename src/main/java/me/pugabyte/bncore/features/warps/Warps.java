package me.pugabyte.bncore.features.warps;

import lombok.Getter;
import me.lucko.helper.item.ItemStackBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Warps {
	@Getter
	private static Location spawn = new Location(Bukkit.getWorld("world"), -1.5, 156.0, -19.5, 90.0F, 0.0F);

	public void spawn(Player player) {
		player.teleport(spawn);
	}

	@Getter
	public enum SurvivalWarp {
		SPAWN(3, 2, new ItemStack(Material.SMOOTH_BRICK, 1, (byte) 3)),
		NORTH(3, 1, Material.BONE_BLOCK),
		EAST(4, 2, Material.PACKED_ICE),
		SOUTH(3, 3, new ItemStack(Material.WOOL, 1, (byte) 7)),
		WEST(2, 2, new ItemStack(Material.LOG, 1, (byte) 1)),
		NORTHEAST(4, 1, Material.COBBLESTONE_STAIRS),
		SOUTHEAST(4, 3, new ItemStack(Material.WOOD, 1, (byte) 2)),
		SOUTHWEST(2, 3, Material.HAY_BLOCK),
		NORTHWEST(2, 1, new ItemStack(Material.DIRT, 1, (byte) 2)),
		NORTH_2(3, 0, Material.WOOD),
		EAST_2(5, 2, Material.LOG),
		SOUTH_2(3, 4, Material.SMOOTH_BRICK),
		WEST_2(1, 2, new ItemStack(Material.WOOD, 1, (byte) 4)),
		NORTHEAST_2(5, 0, new ItemStack(Material.STAINED_CLAY, 1, (byte) 7)),
		SOUTHEAST_2(5, 4, Material.SNOW_BLOCK),
		SOUTHWEST_2(1, 4, Material.SAND),
		NORTHWEST_2(1, 0, new ItemStack(Material.LOG, 1, (byte) 3)),
		NETHER(0, 4, Material.NETHERRACK);

		private int row;
		private int column;
		private ItemStack itemStack;

		SurvivalWarp(int row, int column, ItemStack itemStack) {
			this.row = row;
			this.column = column;
			this.itemStack = itemStack;
		}

		SurvivalWarp(int row, int column, Material material) {
			this.row = row;
			this.column = column;
			this.itemStack = new ItemStack(material);
		}

		public String getDisplayName() {
			return StringUtils.camelCase(name().replace("_", " #"));
		}

		public ItemStack getMenuItem() {
			return ItemStackBuilder.of(itemStack).name("&3" + getDisplayName()).build();
		}

	}

}
