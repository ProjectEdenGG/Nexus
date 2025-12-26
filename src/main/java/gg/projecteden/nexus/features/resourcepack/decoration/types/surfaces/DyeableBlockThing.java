package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class DyeableBlockThing extends DyeableFloorThing {

	public DyeableBlockThing(String name, ItemModelType itemModelType, RotationSnap rotationSnap) {
		super(false, name, itemModelType, ColorableType.DYE, HitboxSingle._1x1_BARRIER);
		this.rotationSnap = rotationSnap;
	}

	public DyeableBlockThing(String name, ItemModelType itemModelType, ColorableType colorableType, RotationSnap rotationSnap) {
		super(false, name, itemModelType, colorableType, HitboxSingle._1x1_BARRIER);
		this.rotationSnap = rotationSnap;
	}
}
