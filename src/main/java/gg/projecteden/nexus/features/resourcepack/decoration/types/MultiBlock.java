package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DisabledRotation;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;

import java.util.List;

public class MultiBlock extends Decoration {
	public MultiBlock(String name, int modelData, List<Hitbox> hitboxes) {
		this.name = name;
		this.modelData = modelData;
		this.hitboxes = hitboxes;
		this.disabledRotation = DisabledRotation.DEGREE_45;
	}

}
