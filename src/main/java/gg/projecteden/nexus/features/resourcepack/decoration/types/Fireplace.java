package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxUnique;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class Fireplace extends FloorThing {

	public Fireplace(boolean multiblock, String name, ItemModelType itemModelType) {
		super(multiblock, name, itemModelType, HitboxUnique.FIREPLACE);
	}
}
