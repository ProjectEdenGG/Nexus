package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class LongChair extends DyeableChair implements Seat, Colorable {

	public LongChair(boolean multiblock, boolean backless, String name, ItemModelType itemModelType, ColorableType colorableType, CustomHitbox hitbox, Double sitHeight) {
		super(multiblock, backless, name, itemModelType, colorableType, hitbox, sitHeight);
	}
}
