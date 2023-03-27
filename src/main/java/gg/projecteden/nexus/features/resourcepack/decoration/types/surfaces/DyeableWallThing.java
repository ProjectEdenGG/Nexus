package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class DyeableWallThing extends Dyeable {

	public DyeableWallThing(String name, CustomMaterial material, ColorableType colorableType, String hexOverride) {
		super(name, material, colorableType, hexOverride);
		this.disabledPlacements = List.of(PlacementType.FLOOR, PlacementType.CEILING);
	}

	public DyeableWallThing(String name, CustomMaterial material, ColorableType colorableType) {
		super(name, material, colorableType);
		this.disabledPlacements = List.of(PlacementType.FLOOR, PlacementType.CEILING);
	}

	public DyeableWallThing(String name, CustomMaterial material, ColorableType colorableType, CustomHitbox hitbox) {
		super(name, material, colorableType, hitbox);
		this.disabledPlacements = List.of(PlacementType.FLOOR, PlacementType.CEILING);
	}

}
