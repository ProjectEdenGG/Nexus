package gg.projecteden.nexus.features.resourcepack.decoration.common;

import lombok.Data;

@Data
public class MobPlushie extends Decoration {
	double dropChance;

	public MobPlushie(String name, int modelData, double dropChance) {
		this.dropChance = dropChance;
		this.modelData = modelData;
		this.name = name;
	}
}
