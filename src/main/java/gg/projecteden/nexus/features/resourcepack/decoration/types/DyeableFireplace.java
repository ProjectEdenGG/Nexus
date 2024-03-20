package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxUnique;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class DyeableFireplace extends DyeableFloorThing {

	public DyeableFireplace(boolean multiblock, String name, CustomMaterial material) {
		super(multiblock, name, material, ColorableType.STAIN, HitboxUnique.FIREPLACE);
	}
}
