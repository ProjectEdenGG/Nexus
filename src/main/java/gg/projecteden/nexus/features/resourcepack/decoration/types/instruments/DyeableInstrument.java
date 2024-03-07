package gg.projecteden.nexus.features.resourcepack.decoration.types.instruments;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.Instrument.InstrumentSound;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class DyeableInstrument extends Dyeable implements NoiseMaker {
	InstrumentSound sound;

	public DyeableInstrument(boolean multiblock, String name, CustomMaterial material, InstrumentSound sound, ColorableType colorableType, CustomHitbox hitbox, PlacementType placementType) {
		super(multiblock, name, material, colorableType, hitbox);
		this.disabledPlacements = placementType.getDisabledPlacements();
		this.sound = sound;
	}

	@Override
	public InstrumentSound getInstrumentSound() {
		return this.sound;
	}

	@Override
	public double getPitch(double lastPitch) {
		return this.sound.getPitch(lastPitch);
	}


}
