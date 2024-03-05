package gg.projecteden.nexus.features.resourcepack.decoration.types.instruments;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.Instrument.InstrumentSound;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.Instrument.InstrumentType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class DyeableInstrument extends Dyeable implements NoiseMaker {
	InstrumentType instrumentType;
	InstrumentSound sound;

	//

	public DyeableInstrument(boolean multiblock, String name, CustomMaterial material, InstrumentSound sound, ColorableType colorableType, CustomHitbox hitbox, InstrumentType instrumentType) {
		super(multiblock, name, material, colorableType, hitbox);
		this.instrumentType = instrumentType;
		this.sound = sound;


		if (instrumentType == InstrumentType.WALL) {
			this.disabledPlacements = List.of(PlacementType.FLOOR, PlacementType.CEILING);
		} else {
			this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
		}
	}

	@Override
	public boolean isWallThing() {
		return this.instrumentType.equals(InstrumentType.WALL);
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
