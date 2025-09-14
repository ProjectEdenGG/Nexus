package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class CeilingThing extends DecorationConfig {

	public CeilingThing(boolean exclusive, boolean multiblock, String name, ItemModelType itemModelType) {
		this(exclusive, multiblock, name, itemModelType, HitboxSingle.NONE);
	}

	public CeilingThing(boolean multiblock, String name, ItemModelType itemModelType) {
		this(multiblock, name, itemModelType, HitboxSingle.NONE);
	}

	public CeilingThing(boolean multiblock, String name, ItemModelType itemModelType, CustomHitbox hitbox) {
		this(false, multiblock, name, itemModelType, hitbox);
	}

	public CeilingThing(boolean exclusive, boolean multiblock, String name, ItemModelType itemModelType, CustomHitbox hitbox) {
		super(exclusive, multiblock, name, itemModelType, hitbox);
		this.disabledPlacements = PlacementType.CEILING.getDisabledPlacements();
	}
}
