package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxFloor;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Flag.PrideFlagType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class Bunting extends WallThing {

	public Bunting(boolean multiblock, String name, ItemModelType itemModelType) {
		this(multiblock, name, itemModelType, HitboxFloor._1x2H_LIGHT);
	}

	public Bunting(boolean multiblock, String name, ItemModelType itemModelType, CustomHitbox hitbox) {
		super(multiblock, name, itemModelType, hitbox);
	}

	public Bunting(String name, PrideFlagType prideFlagType) {
		super(false, name, prideFlagType.getBunting());
	}
}
