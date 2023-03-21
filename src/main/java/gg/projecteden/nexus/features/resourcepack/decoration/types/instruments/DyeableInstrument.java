package gg.projecteden.nexus.features.resourcepack.decoration.types.instruments;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.Shape;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.Instrument.InstrumentSound;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.Instrument.InstrumentType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class DyeableInstrument extends Dyeable implements NoiseMaker {
	InstrumentType instrumentType;
	InstrumentSound sound;
	boolean multiBlock;

	public DyeableInstrument(String name, CustomMaterial material, InstrumentSound sound, ColorableType colorableType, InstrumentType instrumentType) {
		this(name, material, sound, colorableType, Shape.NONE, false, instrumentType);
	}

	public DyeableInstrument(String name, CustomMaterial material, InstrumentSound sound, ColorableType colorableType, CustomHitbox hitbox, InstrumentType instrumentType) {
		this(name, material, sound, colorableType, hitbox, false, instrumentType);
	}

	//

	public DyeableInstrument(String name, CustomMaterial material, InstrumentSound sound, ColorableType colorableType, CustomHitbox hitbox, boolean multiBlock, InstrumentType instrumentType) {
		super(name, material, colorableType, hitbox.getHitboxes());
		this.multiBlock = multiBlock;
		this.sound = sound;

		if (this.multiBlock) {
			this.rotatable = false;
			this.rotationType = RotationType.DEGREE_90;
		}

		if (instrumentType == InstrumentType.WALL) {
			this.rotatable = false;
			this.rotationType = RotationType.DEGREE_90;
			this.disabledPlacements = List.of(PlacementType.FLOOR, PlacementType.CEILING);
		} else {
			this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
		}
	}

	@Override
	public String getSound() {
		return this.sound.getSound();
	}

	@Override
	public double getPitch(double lastPitch) {
		return this.sound.getPitch(lastPitch);
	}

	@Override
	public boolean isMultiBlock() {
		return this.multiBlock;
	}
}
