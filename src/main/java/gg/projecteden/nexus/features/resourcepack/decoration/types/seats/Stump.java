package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class Stump extends Chair {

	public Stump(boolean multiblock, String name, ItemModelType itemModelType) {
		this(multiblock, name, itemModelType, null);
	}

	public Stump(boolean multiblock, String name, ItemModelType itemModelType, Double sitHeight) {
		super(multiblock, true, name, itemModelType, HitboxSingle._1x1_POT, sitHeight);
	}

}
