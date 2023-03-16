package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxShape;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class WorkBench extends FloorThing {
	private final boolean multiBlock;

	@Override
	public boolean isMultiBlock() {
		return this.multiBlock;
	}

	public WorkBench(String name, CustomMaterial material) {
		this(name, material, HitboxShape._1x1.getHitboxes(), false);
	}

	public WorkBench(String name, CustomMaterial material, HitboxShape shape) {
		this(name, material, shape.getHitboxes(), true);
	}


	public WorkBench(String name, CustomMaterial material, List<Hitbox> hitboxes) {
		this(name, material, hitboxes, true);
	}

	private WorkBench(String name, CustomMaterial material, List<Hitbox> hitboxes, boolean multiBlock) {
		super(name, material, hitboxes);
		this.multiBlock = multiBlock;
		this.rotationType = RotationType.DEGREE_90;
	}
}
