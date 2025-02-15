package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class Block extends FloorThing {
	public Block(String name, ItemModelType itemModelType, RotationSnap rotationSnap) {
		super(false, name, itemModelType, HitboxSingle._1x1_BARRIER);
		this.rotationSnap = rotationSnap;
	}
}
