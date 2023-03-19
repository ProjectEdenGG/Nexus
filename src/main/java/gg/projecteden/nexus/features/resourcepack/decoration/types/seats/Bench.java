package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

@MultiBlock
public class Bench extends Chair implements Seat, Colorable {
	public Bench(String name, CustomMaterial material, ColorableType colorableType, CustomHitbox hitbox) {
		super(name, material, colorableType, hitbox.getHitboxes(), null);
	}

	public Bench(String name, CustomMaterial material, ColorableType colorableType, double sitHeight, CustomHitbox hitbox) {
		super(name, material, colorableType, hitbox.getHitboxes(), sitHeight);
	}
}
