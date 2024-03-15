package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class Stump extends Chair {

	public Stump(boolean multiblock, String name, CustomMaterial material) {
		this(multiblock, name, material, null);
	}

	public Stump(boolean multiblock, String name, CustomMaterial material, Double sitHeight) {
		super(multiblock, true, name, material, ColorableType.NONE, HitboxSingle._1x1_POT, sitHeight);
	}

}
