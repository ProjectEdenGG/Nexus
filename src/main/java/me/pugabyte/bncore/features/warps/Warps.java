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
		SPAWN(3, 2, new ItemStack(Material.CHISELED_STONE_BRICKS)),
		NORTH(3, 1, Material.BONE_BLOCK),
		EAST(4, 2, Material.PACKED_ICE),
		SOUTH(3, 3, new ItemStack(Material.GRAY_WOOL)),
		WEST(2, 2, new ItemStack(Material.SPRUCE_LOG)),
		NORTHEAST(4, 1, Material.COBBLESTONE_STAIRS),
		SOUTHEAST(4, 3, new ItemStack(Material.BIRCH_PLANKS)),
		SOUTHWEST(2, 3, Material.HAY_BLOCK),
		NORTHWEST(2, 1, new ItemStack(Material.PODZOL)),
		NORTH_2(3, 0, Material.OAK_PLANKS),
		EAST_2(5, 2, Material.OAK_LOG),
		SOUTH_2(3, 4, Material.STONE_BRICKS),
		WEST_2(1, 2, new ItemStack(Material.ACACIA_PLANKS)),
		NORTHEAST_2(5, 0, new ItemStack(Material.GRAY_TERRACOTTA)),
		SOUTHEAST_2(5, 4, Material.SNOW_BLOCK),
		SOUTHWEST_2(1, 4, Material.SAND),
		NORTHWEST_2(1, 0, new ItemStack(Material.JUNGLE_PLANKS)),
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
