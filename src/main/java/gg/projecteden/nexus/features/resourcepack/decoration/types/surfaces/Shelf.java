package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

@MultiBlock
public class Shelf extends DyeableWallThing {

	public Shelf(String name, CustomMaterial material, ColorableType colorableType, List<Hitbox> hitboxes) {
		super(name, material, colorableType, hitboxes);
	}
}
