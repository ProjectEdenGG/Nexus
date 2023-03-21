package gg.projecteden.nexus.features.resourcepack.decoration.types.instruments;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.Shape;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.MathUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Instrument extends DecorationConfig implements NoiseMaker {
	InstrumentType instrumentType;
	InstrumentSound sound;
	boolean multiBlock; // TODO: IF NEEDED

	public Instrument(String name, CustomMaterial material, InstrumentSound sound, InstrumentType instrumentType) {
		this(name, material, sound, Shape.NONE, instrumentType);
	}

	//

	public Instrument(String name, CustomMaterial material, InstrumentSound sound, CustomHitbox hitbox, InstrumentType instrumentType) {
		super(name, material, hitbox.getHitboxes());
		this.sound = sound;
		this.instrumentType = instrumentType;

		if (instrumentType == InstrumentType.WALL) {
			this.rotatable = false;
			this.rotationType = RotationType.DEGREE_90;
			this.disabledPlacements = List.of(PlacementType.FLOOR, PlacementType.CEILING);
		} else {
			this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
		}
	}

	@Override
	public @Nullable String getSound() {
		return this.sound.getSound();
	}

	@Override
	public double getPitch(double lastPitch) {
		return sound.getPitch(lastPitch);
	}

	@Override
	public boolean isWallThing() {
		return this.instrumentType.equals(InstrumentType.WALL);
	}

	public enum InstrumentType {
		WALL,
		FLOOR
	}

	@AllArgsConstructor
	public enum InstrumentSound {
		TODO(null),

		DRUM_KIT("custom.instrument.drum_kit"),
		GRAND_PIANO("custom.instrument.grand_piano") {
			@Override
			double getPitch(double lastPitch) {
				return SoundUtils.getPitch(RandomUtils.randomElement(cMScale));
			}
		},
		PIANO("block.note_block.harp") {
			@Override
			double getPitch(double lastPitch) {
				return SoundUtils.getPitch(RandomUtils.randomElement(cMScale));
			}
		},
		HARP("custom.instrument.harp") {
			@Override
			double getPitch(double lastPitch) {
				return SoundUtils.getPitch(RandomUtils.randomElement(cMScale));
			}
		},
		BONGOS("custom.instrument.bongos"),
		;

		@Getter
		@Nullable
		final String sound;
		private static final List<Integer> cMScale = List.of(1, 3, 5, 6, 8, 10, 11, 13, 15, 17, 18, 20, 22, 23);


		double getPitch(double lastPitch) {
			AtomicReference<Double> pitch = new AtomicReference<>((double) 0);
			Utils.attempt(10, () -> {
				double newPitch = lastPitch + RandomUtils.randomDouble(-0.30, 0.30);

				newPitch = MathUtils.round(MathUtils.clamp(newPitch, 0.10, 2.00), 2);

				if (newPitch == lastPitch)
					return false;

				pitch.set(newPitch);
				return true;
			});

			return pitch.get();
		}
	}


}
