package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxFloor;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class HangingBanner extends WallThing {

	public HangingBanner(String name, CustomMaterial material) {
		this(name, material, HitboxFloor._1x2V_LIGHT_DOWN);
	}

	public HangingBanner(String name, CustomMaterial material, CustomHitbox hitbox) {
		super(true, name, material, hitbox);
	}

}
