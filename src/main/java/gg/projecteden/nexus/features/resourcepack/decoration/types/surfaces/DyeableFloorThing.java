package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class DyeableFloorThing extends Dyeable {

	public DyeableFloorThing(String name, CustomMaterial material, Type type, String hexOverride) {
		super(name, material, type, hexOverride);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}

	public DyeableFloorThing(String name, CustomMaterial material, Type type) {
		super(name, material, type);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}

	public DyeableFloorThing(String name, CustomMaterial material, Type type, List<Hitbox> hitboxes) {
		super(name, material, type, hitboxes);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}
}
