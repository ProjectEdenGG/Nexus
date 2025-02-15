package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class Bench extends DyeableChair implements Seat, Colorable {
	public Bench(boolean multiblock, boolean backless, String name, ItemModelType itemModelType, ColorableType colorableType, CustomHitbox hitbox) {
		super(multiblock, backless, name, itemModelType, colorableType, hitbox, null);
	}

	public Bench(boolean multiblock, boolean backless, String name, ItemModelType itemModelType, ColorableType colorableType, double sitHeight, CustomHitbox hitbox) {
		super(multiblock, backless, name, itemModelType, colorableType, hitbox, sitHeight);
	}


}
