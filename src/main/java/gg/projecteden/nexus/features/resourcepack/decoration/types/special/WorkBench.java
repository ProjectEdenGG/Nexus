package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Interactable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class WorkBench extends FloorThing implements Interactable {
	private final boolean multiBlock;

	public WorkBench(String name, CustomMaterial material) {
		this(name, material, HitboxSingle._1x1, false);
	}

	public WorkBench(String name, CustomMaterial material, CustomHitbox hitbox) {
		this(name, material, hitbox, true);
	}


	private WorkBench(String name, CustomMaterial material, CustomHitbox hitbox, boolean multiBlock) {
		super(name, material, hitbox);
		this.multiBlock = multiBlock;
		this.rotationSnap = RotationSnap.DEGREE_90;
		if (this.multiBlock) {
			this.rotatable = false;
		}
	}

	@Override
	public boolean isMultiBlock() {
		return this.multiBlock;
	}
}
