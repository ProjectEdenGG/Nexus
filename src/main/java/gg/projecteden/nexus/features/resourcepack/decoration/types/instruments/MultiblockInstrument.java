package gg.projecteden.nexus.features.resourcepack.decoration.types.instruments;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

@MultiBlock
public class MultiblockInstrument extends DyeableInstrument {

	public MultiblockInstrument(String name, CustomMaterial material, ColorableType type, CustomHitbox hitbox) {
		this(name, material, null, type, hitbox);
	}

	public MultiblockInstrument(String name, CustomMaterial material, String sound, ColorableType type, CustomHitbox hitbox) {
		super(name, material, sound, type, null, hitbox.getHitboxes());
		this.rotationType = RotationType.DEGREE_90;
	}
}
