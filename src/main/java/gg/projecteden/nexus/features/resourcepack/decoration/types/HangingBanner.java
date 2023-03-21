package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.Unique;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

@MultiBlock
public class HangingBanner extends WallThing {

	public HangingBanner(String name, CustomMaterial material) {
		this(name, material, Unique.HANGING_BANNER_1x2V);
	}

	public HangingBanner(String name, CustomMaterial material, CustomHitbox hitbox) {
		super(name, material, hitbox.getHitboxes());
	}

}
