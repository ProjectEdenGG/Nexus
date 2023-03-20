package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSettings;
import gg.projecteden.nexus.features.resourcepack.decoration.common.NoiseMaker;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class Instrument extends FloorThing implements NoiseMaker {
	String sound = null;

	@Override
	public String getSound() {
		return this.sound;
	}

	public Instrument(String name, CustomMaterial material) {
		super(name, material);
	}

	public Instrument(String name, CustomMaterial material, CustomHitbox hitbox) {
		super(name, material, hitbox.getHitboxes());
	}

	public Instrument(String name, CustomMaterial material, HitboxSettings hitboxSettings) {
		super(name, material, hitboxSettings.getHitboxes());
	}

	public Instrument(String name, CustomMaterial material, String sound) {
		super(name, material);
		this.sound = sound;
	}

	public Instrument(String name, CustomMaterial material, String sound, CustomHitbox hitbox) {
		super(name, material, hitbox.getHitboxes());
		this.sound = sound;
	}


}
