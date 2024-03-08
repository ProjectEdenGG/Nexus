package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class Bench extends Chair implements Seat, Colorable {
	public Bench(boolean multiblock, String name, CustomMaterial material, ColorableType colorableType, CustomHitbox hitbox) {
		super(multiblock, name, material, colorableType, hitbox, null);
	}

	public Bench(boolean multiblock, String name, CustomMaterial material, ColorableType colorableType, double sitHeight, CustomHitbox hitbox) {
		super(multiblock, name, material, colorableType, hitbox, sitHeight);
	}
}
