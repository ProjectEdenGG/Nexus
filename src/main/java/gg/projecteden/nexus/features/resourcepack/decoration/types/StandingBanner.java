package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxFloor;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class StandingBanner extends FloorThing {

	public StandingBanner(String name, ItemModelType itemModelType) {
		this(name, itemModelType, HitboxFloor._1x2V_LIGHT);
	}

	public StandingBanner(String name, ItemModelType itemModelType, CustomHitbox hitbox) {
		super(false, name, itemModelType, hitbox);
	}
}
