package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import lombok.Getter;

public class MobPlushie extends Decoration {
	@Getter
	private final double dropChance;

	public MobPlushie(String name, int modelData, double dropChance) {
		super(name, modelData);
		this.dropChance = dropChance;
	}
}
