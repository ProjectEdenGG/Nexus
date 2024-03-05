package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class WallThing extends DecorationConfig {

	public WallThing(boolean multiblock, String name, CustomMaterial material) {
		this(multiblock, name, material, HitboxSingle.NONE);
	}

	public WallThing(boolean multiblock, String name, CustomMaterial material, CustomHitbox hitbox) {
		super(multiblock, name, material, hitbox);
		this.disabledPlacements = List.of(PlacementType.FLOOR, PlacementType.CEILING);
	}
}
