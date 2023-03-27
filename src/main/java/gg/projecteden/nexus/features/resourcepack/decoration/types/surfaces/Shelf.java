package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

@MultiBlock
public class Shelf extends DyeableWallThing {

	public Shelf(String name, CustomMaterial material, ColorableType colorableType, CustomHitbox hitboxes) {
		super(name, material, colorableType, hitboxes);
	}
}
