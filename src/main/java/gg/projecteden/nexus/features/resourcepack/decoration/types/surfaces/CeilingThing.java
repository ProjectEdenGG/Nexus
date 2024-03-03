package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class CeilingThing extends DecorationConfig {

	public CeilingThing(String name, CustomMaterial material) {
		this(name, material, HitboxSingle.NONE);
	}

	public CeilingThing(String name, CustomMaterial material, CustomHitbox hitbox) {
		super(name, material, hitbox);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.FLOOR);
	}
}
