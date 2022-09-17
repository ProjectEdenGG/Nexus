package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class DyeableFloorThing extends Dyeable {

	public DyeableFloorThing(String name, CustomMaterial material, Colorable.Type type, String hexOverride) {
		super(name, material, type, hexOverride);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}

	public DyeableFloorThing(String name, CustomMaterial material, Colorable.Type type) {
		super(name, material, type);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}
}
