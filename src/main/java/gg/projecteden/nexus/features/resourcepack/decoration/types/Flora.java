package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class Flora extends Dyeable {

	public Flora(boolean multiblock, String name, CustomMaterial material, PlacementType placementType) {
		this(multiblock, name, material, HitboxSingle.NONE, placementType);
	}

	public Flora(boolean multiblock, String name, CustomMaterial material, CustomHitbox hitbox, PlacementType placementType) {
		super(multiblock, name, material, ColorableType.DYE, hitbox);
		this.disabledPlacements = placementType.getDisabledPlacements();

		if (this.disabledPlacements.contains(PlacementType.WALL))
			this.rotatable = false;
	}


}
