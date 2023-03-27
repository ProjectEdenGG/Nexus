package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.Basic;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class Block extends FloorThing {
	public Block(String name, CustomMaterial material, RotationType rotationType) {
		super(name, material, Basic._1x1);
		this.rotationType = rotationType;
	}
}
