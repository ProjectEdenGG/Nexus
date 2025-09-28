package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class Bench extends Chair implements Seat {

	public Bench(boolean multiblock, boolean backless, String name, ItemModelType itemModelType, CustomHitbox hitbox) {
		super(multiblock, backless, name, itemModelType, hitbox, null);
	}

	public Bench(boolean multiblock, boolean backless, String name, ItemModelType itemModelType, double sitHeight, CustomHitbox hitbox) {
		super(multiblock, backless, name, itemModelType, hitbox, sitHeight);
	}
}
