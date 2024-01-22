package gg.projecteden.nexus.features.warps;

import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class Warps {
	@Getter
	private static final Location hub = WarpType.NORMAL.get("hub").getLocation();
	@Getter
	private static final Location survival = WarpType.NORMAL.get("survival").getLocation();

	public static @NotNull CompletableFuture<Boolean> hub(Player player) {
		return player.teleportAsync(hub, TeleportCause.PLUGIN);
	}
	public static @NotNull CompletableFuture<Boolean> survival(Player player) {
		return player.teleportAsync(survival, TeleportCause.PLUGIN);
	}

	@Getter
	public enum SurvivalWarp {
		SPAWN(3, 2, Material.PRISMARINE),
		NORTH(3, 1, Material.OAK_BOAT),
		EAST(4, 2, Material.RED_MUSHROOM_BLOCK),
		SOUTH(3, 3, Material.CAMPFIRE),
		WEST(2, 2, Material.STONE_BRICKS),
//		NETHER(0, 4, Material.NETHERRACK)
		;

		private final int row;
		private final int column;
		private final ItemStack itemStack;

		SurvivalWarp(int row, int column, Material material) {
			this.row = row;
			this.column = column;
			this.itemStack = new ItemStack(material);
		}

		public String getDisplayName() {
			return StringUtils.camelCase(name().replace("_", " #"));
		}

		public ItemStack getMenuItem() {
			return new ItemBuilder(itemStack).name("&3" + getDisplayName()).build();
		}

	}

	@Getter
	public enum LegacySurvivalWarp {
		SPAWN(3, 2, Material.CHISELED_STONE_BRICKS),
		NORTH(3, 1, Material.BONE_BLOCK),
		EAST(4, 2, Material.PACKED_ICE),
		SOUTH(3, 3, Material.GRAY_WOOL),
		WEST(2, 2, Material.SPRUCE_LOG),
		NORTHEAST(4, 1, Material.COBBLESTONE_STAIRS),
		SOUTHEAST(4, 3, Material.BIRCH_PLANKS),
		SOUTHWEST(2, 3, Material.HAY_BLOCK),
		NORTHWEST(2, 1, Material.PODZOL),
		NORTH_2(3, 0, Material.OAK_PLANKS),
		EAST_2(5, 2, Material.OAK_LOG),
		SOUTH_2(3, 4, Material.STONE_BRICKS),
		WEST_2(1, 2, Material.ACACIA_PLANKS),
		NORTHEAST_2(5, 0, Material.GRAY_TERRACOTTA),
		SOUTHEAST_2(5, 4, Material.SNOW_BLOCK),
		SOUTHWEST_2(1, 4, Material.SAND),
		NORTHWEST_2(1, 0, Material.JUNGLE_PLANKS),
		NETHER(0, 4, Material.NETHERRACK);

		private final int row;
		private final int column;
		private final ItemStack itemStack;

		LegacySurvivalWarp(int row, int column, Material material) {
			this.row = row;
			this.column = column;
			this.itemStack = new ItemStack(material);
		}

		public String getDisplayName() {
			return StringUtils.camelCase(name().replace("_", " #"));
		}

		public ItemStack getMenuItem() {
			return new ItemBuilder(itemStack).name("&3" + getDisplayName()).build();
		}

	}

}
