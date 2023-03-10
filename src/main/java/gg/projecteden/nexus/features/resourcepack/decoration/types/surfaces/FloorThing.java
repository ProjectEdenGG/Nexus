package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxShape;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class FloorThing extends DecorationConfig {

	public FloorThing(String name, CustomMaterial material) {
		super(name, material);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}

	public FloorThing(String name, CustomMaterial material, List<Hitbox> hitboxes) {
		super(name, material, hitboxes);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}

	public FloorThing(String name, CustomMaterial material, HitboxShape shape) {
		super(name, material, shape.getHitboxes());
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}
}
