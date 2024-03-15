package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class LongChair extends Chair implements Seat, Colorable {

	public LongChair(boolean multiblock, boolean backless, String name, CustomMaterial material, ColorableType colorableType, CustomHitbox hitbox, Double sitHeight) {
		super(multiblock, backless, name, material, colorableType, hitbox, sitHeight);
	}
}
