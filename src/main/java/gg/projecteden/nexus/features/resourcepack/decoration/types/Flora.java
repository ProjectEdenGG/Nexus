package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class Flora extends Dyeable {

	public Flora(boolean multiblock, String name, ItemModelType itemModelType, PlacementType placementType) {
		this(multiblock, name, itemModelType, HitboxSingle.NONE, placementType);
	}

	public Flora(boolean multiblock, String name, ItemModelType itemModelType, CustomHitbox hitbox, PlacementType placementType) {
		super(multiblock, name, itemModelType, ColorableType.DYE, hitbox);
		this.disabledPlacements = placementType.getDisabledPlacements();

		if (placementType == PlacementType.WALL)
			this.rotatable = false;
	}


}
