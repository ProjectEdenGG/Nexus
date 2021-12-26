package gg.projecteden.nexus.features.resourcepack.decoration.common;

import java.util.List;

public class MultiBlockSeat extends Seat {

	public MultiBlockSeat(String name, int modelData, List<Hitbox> hitboxes) {
		super(name, modelData, hitboxes);
		this.disabledRotation = DisabledRotation.DEGREE_45;
	}
}
