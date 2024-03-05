package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.Getter;

public class MobPlushie extends FloorThing {
	@Getter
	private final double dropChance;

	public MobPlushie(String name, CustomMaterial material, double dropChance) {
		super(false, name, material);
		this.dropChance = dropChance;
	}
}
