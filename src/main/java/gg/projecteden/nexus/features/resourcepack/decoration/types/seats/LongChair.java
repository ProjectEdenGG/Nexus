package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox.LightHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxUnique;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

@MultiBlock
public class LongChair extends Chair implements Seat, Colorable {

	public LongChair(String name, CustomMaterial material, ColorableType colorableType, LightHitbox lightHitbox, Double sitHeight) {
		super(name, material, colorableType, HitboxUnique.BEACH_CHAIR, sitHeight);
	}
}
