package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxUnique;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class Fireplace extends FloorThing {

	public Fireplace(boolean multiblock, String name, CustomMaterial material) {
		super(multiblock, name, material, HitboxUnique.FIREPLACE);
	}
}
