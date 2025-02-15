package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.Getter;

public class MobPlushie extends FloorThing {
	@Getter
	private final double dropChance;

	public MobPlushie(String name, ItemModelType itemModelType, double dropChance) {
		super(false, name, itemModelType);
		this.dropChance = dropChance;
	}
}
