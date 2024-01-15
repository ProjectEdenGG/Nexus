package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.Unique;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

@MultiBlock
public class Fireplace extends FloorThing {

	public Fireplace(String name, CustomMaterial material) {
		super(name, material, Unique.FIREPLACE);
		this.rotationSnap = RotationSnap.DEGREE_90;
	}
}
