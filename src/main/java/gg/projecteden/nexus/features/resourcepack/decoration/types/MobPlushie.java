package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import lombok.Data;

@Data
public class MobPlushie extends Decoration {
	double dropChance;

	public MobPlushie(String name, int modelData, double dropChance) {
		this.name = name;
		this.modelData = modelData;
		this.dropChance = dropChance;

	}
}
