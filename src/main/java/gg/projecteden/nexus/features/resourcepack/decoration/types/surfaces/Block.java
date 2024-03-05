package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class Block extends FloorThing {
	public Block(String name, CustomMaterial material, RotationSnap rotationSnap) {
		super(false, name, material, HitboxSingle._1x1);
		this.rotationSnap = rotationSnap;
	}
}
