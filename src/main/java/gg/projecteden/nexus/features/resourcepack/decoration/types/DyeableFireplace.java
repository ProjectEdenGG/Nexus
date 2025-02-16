package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxUnique;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class DyeableFireplace extends DyeableFloorThing {

	public DyeableFireplace(boolean multiblock, String name, ItemModelType itemModelType) {
		super(multiblock, name, itemModelType, ColorableType.STAIN, HitboxUnique.FIREPLACE);
	}
}
