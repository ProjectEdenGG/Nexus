package gg.projecteden.nexus.features.resourcepack.decoration.common;

import java.util.List;

public class MultiBlock extends Decoration {
	public MultiBlock(String name, int modelData, List<Hitbox> hitboxes) {
		this.name = name;
		this.modelData = modelData;
		this.hitboxes = hitboxes;
		this.disabledRotation = DisabledRotation.DEGREE_45;
	}

}
