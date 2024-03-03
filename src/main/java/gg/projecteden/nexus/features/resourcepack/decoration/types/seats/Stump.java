package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class Stump extends Chair {

	public Stump(String name, CustomMaterial material) {
		this(name, material, null);
	}

	public Stump(String name, CustomMaterial material, Double sitHeight) {
		super(name, material, ColorableType.NONE, HitboxSingle._1x1_POT, sitHeight);
	}

}
