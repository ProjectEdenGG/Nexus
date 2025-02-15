package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class FloorThing extends DecorationConfig {

	public FloorThing(boolean multiblock, String name, ItemModelType itemModelType) {
		this(multiblock, name, itemModelType, HitboxSingle.NONE);
	}

	public FloorThing(boolean multiblock, String name, ItemModelType itemModelType, CustomHitbox hitbox) {
		super(multiblock, name, itemModelType, hitbox);
		this.disabledPlacements = PlacementType.FLOOR.getDisabledPlacements();
	}
}
