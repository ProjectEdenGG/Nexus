package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Interactable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class WorkBench extends FloorThing implements Interactable {

	public WorkBench(String name, CustomMaterial material) {
		this(false, name, material, HitboxSingle._1x1);
	}

	public WorkBench(String name, CustomMaterial material, CustomHitbox hitbox) {
		this(true, name, material, hitbox);
	}

	private WorkBench(boolean multiblock, String name, CustomMaterial material, CustomHitbox hitbox) {
		super(multiblock, name, material, hitbox);
	}
}
