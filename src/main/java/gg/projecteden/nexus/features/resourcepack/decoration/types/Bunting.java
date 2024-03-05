package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxFloor;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class Bunting extends WallThing {

	public Bunting(boolean multiblock, String name, CustomMaterial material) {
		this(multiblock, name, material, HitboxFloor._1x2H_LIGHT);
	}

	public Bunting(boolean multiblock, String name, CustomMaterial material, CustomHitbox hitbox) {
		super(multiblock, name, material, hitbox);
	}
}
