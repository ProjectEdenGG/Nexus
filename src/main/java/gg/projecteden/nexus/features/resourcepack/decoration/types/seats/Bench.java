package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class Bench extends DyeableChair implements Seat, Colorable {
	public Bench(boolean multiblock, boolean backless, String name, CustomMaterial material, ColorableType colorableType, CustomHitbox hitbox) {
		super(multiblock, backless, name, material, colorableType, hitbox, null);
	}

	public Bench(boolean multiblock, boolean backless, String name, CustomMaterial material, ColorableType colorableType, double sitHeight, CustomHitbox hitbox) {
		super(multiblock, backless, name, material, colorableType, hitbox, sitHeight);
	}


}
