package gg.projecteden.nexus.features.resourcepack.decoration.types.instruments;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class DyeableInstrument extends DyeableFloorThing implements NoiseMaker {
	String sound;

	@Override
	public String getSound() {
		return this.sound;
	}

	public DyeableInstrument(String name, CustomMaterial material, ColorableType type) {
		super(name, material, type);
	}

	public DyeableInstrument(String name, CustomMaterial material, ColorableType type, CustomHitbox hitbox) {
		super(name, material, type, hitbox);
	}

	public DyeableInstrument(String name, CustomMaterial material, String sound, ColorableType type) {
		super(name, material, type);
		this.sound = sound;
	}

	public DyeableInstrument(String name, CustomMaterial material, String sound, ColorableType type, String hexOverride, List<Hitbox> hitboxes) {
		super(name, material, type, hexOverride, hitboxes);
		this.sound = sound;
	}


}
