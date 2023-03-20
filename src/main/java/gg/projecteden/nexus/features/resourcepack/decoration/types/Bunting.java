package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.Shape;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

@MultiBlock
public class Bunting extends WallThing {

	public Bunting(String name, CustomMaterial material) {
		this(name, material, Shape._1x2H_LIGHT);
	}

	public Bunting(String name, CustomMaterial material, CustomHitbox customHitbox) {
		super(name, material, customHitbox.getHitboxes());
	}
}
