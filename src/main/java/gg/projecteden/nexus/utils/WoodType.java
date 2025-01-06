package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World.Environment;

@Getter
@AllArgsConstructor
public enum WoodType {
	OAK(Environment.NORMAL),
	SPRUCE(Environment.NORMAL),
	BIRCH(Environment.NORMAL),
	JUNGLE(Environment.NORMAL),
	ACACIA(Environment.NORMAL),
	DARK_OAK(Environment.NORMAL),
	MANGROVE(Environment.NORMAL) {
		@Override
		public Material getSapling() {
			return Material.MANGROVE_PROPAGULE;
		}
	},
	CRIMSON(Environment.NETHER),
	WARPED(Environment.NETHER),
	CHERRY(Environment.NORMAL)
	;

	WoodType(Environment environment) {
		this.environment = environment;

		switch (environment) {
			case NORMAL -> {
				this.log = Material.matchMaterial(name() + "_LOG");
				this.wood = Material.matchMaterial(name() + "_WOOD");
			}
			case NETHER -> {
				this.log = Material.matchMaterial(name() + "_STEM");
				this.wood = Material.matchMaterial(name() + "_HYPHAE");
			}
			default ->
				throw new InvalidInputException("Unsupported Dimension (" + environment.name() + ")");
		}
	}

	private final Environment environment;
	private final Material log;
	private final Material wood;

	public Material getLeaves() {
		return switch (environment) {
			case NORMAL -> Material.matchMaterial(name() + "_LEAVES");
			case NETHER -> Material.matchMaterial((this == CRIMSON ? "NETHER" : name()) + "_WART_BLOCK");
			default -> throw new InvalidInputException("Unsupported Dimension (" + environment.name() + ")");
		};
	}

	public Material getSapling() {
		return switch (environment) {
			case NORMAL -> Material.matchMaterial(name() + "_SAPLING");
			case NETHER -> Material.matchMaterial(name() + "_FUNGUS");
			default -> throw new InvalidInputException("Unsupported Dimension (" + environment.name() + ")");
		};
	}

	public Material getPlanks() {
		return Material.matchMaterial(name() + "_PLANKS");
	}

	public Material getStrippedLog() {
		return Material.matchMaterial("STRIPPED_" + log.name());
	}

	public Material getStrippedWood() {
		return Material.matchMaterial("STRIPPED_" + wood.name());
	}

	public Material getSlab() {
		return Material.matchMaterial(name() + "_SLAB");
	}

	public Material getStair() {
		return Material.matchMaterial(name() + "_STAIRS");
	}

	public Material getFence() {
		return Material.matchMaterial(name() + "_FENCE");
	}

	public Material getFenceGate() {
		return Material.matchMaterial(name() + "_FENCE_GATE");
	}

	public Material getButton() {
		return Material.matchMaterial(name() + "_BUTTON");
	}

	public Material getPressurePlate() {
		return Material.matchMaterial(name() + "_PRESSURE_PLATE");
	}

	public Material getDoor() {
		return Material.matchMaterial(name() + "_DOOR");
	}

	public Material getTrapDoor() {
		return Material.matchMaterial(name() + "_TRAPDOOR");
	}

	public Material getSign() {
		return Material.matchMaterial(name() + "_SIGN");
	}

	public Material getBoat() {
		if (environment == Environment.NETHER)
			return null;

		return Material.matchMaterial(name() + "_BOAT");
	}

	public Material getChestBoat() {
		if (environment == Environment.NETHER)
			return null;

		return Material.matchMaterial(name() + "_CHEST_BOAT");
	}

	public static Material saplingOfTree(TreeType treeType) {
		return switch (treeType) {
			case TREE, BIG_TREE, SWAMP -> Material.OAK_SAPLING;
			case REDWOOD, TALL_REDWOOD, MEGA_REDWOOD, MEGA_PINE -> Material.SPRUCE_SAPLING;
			case BIRCH, TALL_BIRCH -> Material.BIRCH_SAPLING;
			case JUNGLE, SMALL_JUNGLE, COCOA_TREE, JUNGLE_BUSH -> Material.JUNGLE_SAPLING;
			case RED_MUSHROOM -> Material.RED_MUSHROOM;
			case BROWN_MUSHROOM -> Material.BROWN_MUSHROOM;
			case ACACIA -> Material.ACACIA_SAPLING;
			case DARK_OAK -> Material.DARK_OAK_SAPLING;
			case CHORUS_PLANT -> Material.CHORUS_FLOWER;
			case CRIMSON_FUNGUS -> Material.CRIMSON_FUNGUS;
			case WARPED_FUNGUS -> Material.WARPED_FUNGUS;
			case AZALEA -> Material.AZALEA; // No way to tell if its flowering or not
			case MANGROVE, TALL_MANGROVE -> Material.MANGROVE_PROPAGULE;
			case CHERRY -> Material.CHERRY_SAPLING;
			case PALE_OAK, PALE_OAK_CREAKING -> Material.PALE_OAK_SAPLING;
		};
	}

}
