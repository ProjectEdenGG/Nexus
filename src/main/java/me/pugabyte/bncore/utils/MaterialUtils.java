package me.pugabyte.bncore.utils;

import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

@SuppressWarnings("unused")
public class MaterialUtils {

	public static DyeColor getDyeColor(Material type) {
		for (DyeColor color : DyeColor.values())
			if (type.toString().startsWith(color.toString())) return color;
		throw new InvalidInputException("Material is not colored");
	}

	public static EntityType getSpawnEggType(Material type) {
		return EntityType.valueOf(type.toString().split("_SPAWN_EGG")[0]);
	}

	public static Material getSpawnEgg(EntityType type) {
		return Material.valueOf(type.toString() + "_SPAWN_EGG");
	}

	public static boolean isBoat(Material type) {
		switch (type) {
			case ACACIA_BOAT:
			case SPRUCE_BOAT:
			case OAK_BOAT:
			case JUNGLE_BOAT:
			case DARK_OAK_BOAT:
			case BIRCH_BOAT:
				return true;
			default:
				break;
		}
		return false;
	}

	public static boolean isButton(Material type) {
		switch (type) {
			case ACACIA_BUTTON:
			case STONE_BUTTON:
			case SPRUCE_BUTTON:
			case OAK_BUTTON:
			case JUNGLE_BUTTON:
			case DARK_OAK_BUTTON:
			case BIRCH_BUTTON:
				return true;
			default:
				break;
		}
		return false;
	}

	public static boolean isDoor(Material type) {
		switch (type) {
			case ACACIA_DOOR:
			case SPRUCE_DOOR:
			case OAK_DOOR:
			case JUNGLE_DOOR:
			case IRON_DOOR:
			case DARK_OAK_DOOR:
			case BIRCH_DOOR:
				return true;
			default:
				break;
		}
		return false;
	}

	public static boolean isFence(Material type) {
		switch (type) {
			case ACACIA_FENCE:
			case SPRUCE_FENCE:
			case OAK_FENCE:
			case NETHER_BRICK_FENCE:
			case JUNGLE_FENCE:
			case DARK_OAK_FENCE:
			case BIRCH_FENCE:
				return true;
			default:
				break;
		}
		return false;
	}

	public static boolean isFenceGate(Material type) {
		switch (type) {
			case ACACIA_FENCE_GATE:
			case SPRUCE_FENCE_GATE:
			case OAK_FENCE_GATE:
			case JUNGLE_FENCE_GATE:
			case DARK_OAK_FENCE_GATE:
			case BIRCH_FENCE_GATE:
				return true;
			default:
				break;
		}
		return false;
	}

	public static boolean isLeaves(Material type) {
		switch (type) {
			case ACACIA_LEAVES:
			case SPRUCE_LEAVES:
			case OAK_LEAVES:
			case JUNGLE_LEAVES:
			case DARK_OAK_LEAVES:
			case BIRCH_LEAVES:
				return true;
			default:
				break;
		}
		return false;
	}

	public static boolean isLog(Material type) {
		switch (type) {
			case ACACIA_LOG:
			case STRIPPED_SPRUCE_LOG:
			case STRIPPED_OAK_LOG:
			case STRIPPED_JUNGLE_LOG:
			case STRIPPED_DARK_OAK_LOG:
			case STRIPPED_BIRCH_LOG:
			case STRIPPED_ACACIA_LOG:
			case SPRUCE_LOG:
			case OAK_LOG:
			case JUNGLE_LOG:
			case DARK_OAK_LOG:
			case BIRCH_LOG:
				return true;
			default:
				break;
		}
		return false;
	}

	public static boolean isPlanks(Material type) {
		switch (type) {
			case ACACIA_PLANKS:
			case SPRUCE_PLANKS:
			case OAK_PLANKS:
			case JUNGLE_PLANKS:
			case DARK_OAK_PLANKS:
			case BIRCH_PLANKS:
				return true;
			default:
				break;
		}
		return false;
	}

	public static boolean isPressurePlate(Material type) {
		switch (type) {
			case ACACIA_PRESSURE_PLATE:
			case STONE_PRESSURE_PLATE:
			case SPRUCE_PRESSURE_PLATE:
			case OAK_PRESSURE_PLATE:
			case LIGHT_WEIGHTED_PRESSURE_PLATE:
			case JUNGLE_PRESSURE_PLATE:
			case HEAVY_WEIGHTED_PRESSURE_PLATE:
			case DARK_OAK_PRESSURE_PLATE:
			case BIRCH_PRESSURE_PLATE:
				return true;
			default:
				break;
		}
		return false;
	}

	public static boolean isSapling(Material type) {
		switch (type) {
			case ACACIA_SAPLING:
			case SPRUCE_SAPLING:
			case POTTED_SPRUCE_SAPLING:
			case POTTED_OAK_SAPLING:
			case POTTED_JUNGLE_SAPLING:
			case POTTED_DARK_OAK_SAPLING:
			case POTTED_BIRCH_SAPLING:
			case POTTED_ACACIA_SAPLING:
			case OAK_SAPLING:
			case JUNGLE_SAPLING:
			case DARK_OAK_SAPLING:
			case BIRCH_SAPLING:
				return true;
			default:
				break;
		}
		return false;
	}

	public static boolean isSlab(Material type) {
		switch (type) {
			case ACACIA_SLAB:
			case STONE_SLAB:
			case STONE_BRICK_SLAB:
			case SPRUCE_SLAB:
			case SANDSTONE_SLAB:
			case RED_SANDSTONE_SLAB:
			case QUARTZ_SLAB:
			case PURPUR_SLAB:
			case PRISMARINE_SLAB:
			case PRISMARINE_BRICK_SLAB:
			case PETRIFIED_OAK_SLAB:
			case OAK_SLAB:
			case NETHER_BRICK_SLAB:
			case JUNGLE_SLAB:
			case DARK_PRISMARINE_SLAB:
			case DARK_OAK_SLAB:
			case COBBLESTONE_SLAB:
			case BRICK_SLAB:
			case BIRCH_SLAB:
				return true;
			default:
				break;
		}
		return false;
	}

	public static boolean isStairs(Material type) {
		switch (type) {
			case ACACIA_STAIRS:
			case STONE_BRICK_STAIRS:
			case SPRUCE_STAIRS:
			case SANDSTONE_STAIRS:
			case RED_SANDSTONE_STAIRS:
			case QUARTZ_STAIRS:
			case PURPUR_STAIRS:
			case PRISMARINE_STAIRS:
			case PRISMARINE_BRICK_STAIRS:
			case OAK_STAIRS:
			case NETHER_BRICK_STAIRS:
			case JUNGLE_STAIRS:
			case DARK_PRISMARINE_STAIRS:
			case DARK_OAK_STAIRS:
			case COBBLESTONE_STAIRS:
			case BRICK_STAIRS:
			case BIRCH_STAIRS:
				return true;
			default:
				break;
		}
		return false;
	}

	public static boolean isTrapdoor(Material type) {
		switch (type) {
			case ACACIA_TRAPDOOR:
			case SPRUCE_TRAPDOOR:
			case OAK_TRAPDOOR:
			case JUNGLE_TRAPDOOR:
			case IRON_TRAPDOOR:
			case DARK_OAK_TRAPDOOR:
			case BIRCH_TRAPDOOR:
				return true;
			default:
				break;
		}
		return false;
	}

	public static boolean isWood(Material type) {
		switch (type) {
			case ACACIA_WOOD:
			case STRIPPED_SPRUCE_WOOD:
			case STRIPPED_OAK_WOOD:
			case STRIPPED_JUNGLE_WOOD:
			case STRIPPED_DARK_OAK_WOOD:
			case STRIPPED_BIRCH_WOOD:
			case STRIPPED_ACACIA_WOOD:
			case SPRUCE_WOOD:
			case OAK_WOOD:
			case JUNGLE_WOOD:
			case DARK_OAK_WOOD:
			case BIRCH_WOOD:
				return true;
			default:
				break;
		}
		return false;
	}

}
