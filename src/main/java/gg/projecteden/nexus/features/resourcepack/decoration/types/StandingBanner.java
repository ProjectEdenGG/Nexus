package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxFloor;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class StandingBanner extends FloorThing {

	public StandingBanner(String name, CustomMaterial material) {
		this(name, material, HitboxFloor._1x2V_LIGHT);
	}

	public StandingBanner(String name, CustomMaterial material, CustomHitbox hitbox) {
		super(false, name, material, hitbox);
	}
}
