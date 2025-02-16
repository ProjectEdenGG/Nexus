package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Interactable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class WorkBench extends FloorThing implements Interactable {

	public WorkBench(String name, ItemModelType itemModelType) {
		this(false, name, itemModelType, HitboxSingle._1x1_BARRIER);
	}

	public WorkBench(String name, ItemModelType itemModelType, CustomHitbox hitbox) {
		this(true, name, itemModelType, hitbox);
	}

	private WorkBench(boolean multiblock, String name, ItemModelType itemModelType, CustomHitbox hitbox) {
		super(multiblock, name, itemModelType, hitbox);
	}
}
