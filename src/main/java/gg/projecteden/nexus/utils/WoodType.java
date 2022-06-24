package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
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
	MANGROVE(Environment.NORMAL),
	CRIMSON(Environment.NETHER),
	WARPED(Environment.NETHER),
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

}
