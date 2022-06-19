package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.Getter;

public class MobPlushie extends DecorationConfig {
	@Getter
	private final double dropChance;

	public MobPlushie(String name, CustomMaterial material, double dropChance) {
		super(name, material);
		this.dropChance = dropChance;
	}
}
