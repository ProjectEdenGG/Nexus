package gg.projecteden.nexus.features.resourcepack.decoration.types.instruments;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundUtils;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Instrument extends DecorationConfig implements NoiseMaker {
	InstrumentType instrumentType;
	InstrumentSound sound;
	boolean multiBlock = false;

	public Instrument(String name, CustomMaterial material, InstrumentSound sound, InstrumentType instrumentType) {
		this(name, material, sound, HitboxSingle.NONE, instrumentType);
	}

	//

	public Instrument(String name, CustomMaterial material, InstrumentSound sound, CustomHitbox hitbox, InstrumentType instrumentType) {
		super(name, material, hitbox);
		this.sound = sound;
		this.instrumentType = instrumentType;

		if (this.multiBlock) {
			this.rotatable = false;
			this.rotationSnap = RotationSnap.DEGREE_90;
		}

		if (instrumentType == InstrumentType.WALL) {
			this.rotatable = false;
			this.rotationSnap = RotationSnap.DEGREE_90;
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
	public boolean isMultiBlock() {
		return this.multiBlock;
	}

	@Override
	public @Nullable InstrumentSound getInstrumentSound() {
		return this.sound;
	}

	@Override
	public double getPitch(double lastPitch) {
		return sound.getPitch(lastPitch);
	}

	public enum InstrumentType {
		WALL,
		FLOOR
	}

	public enum InstrumentSound {
		DRUM_KIT(CustomSound.DECOR_INSTRUMENT_DRUMS),
		GRAND_PIANO(CustomSound.DECOR_INSTRUMENT_GRAND_PIANO) {
			@Override
			double getPitch(double lastPitch) {
				return SoundUtils.getPitch(RandomUtils.randomElement(cMScale));
			}
		},
		PIANO(Sound.BLOCK_NOTE_BLOCK_HARP) {
			@Override
			double getPitch(double lastPitch) {
				return SoundUtils.getPitch(RandomUtils.randomElement(cMScale));
			}
		},
		HARP(CustomSound.DECOR_INSTRUMENT_HARP) {
			@Override
			double getPitch(double lastPitch) {
				return SoundUtils.getPitch(RandomUtils.randomElement(cMScale));
			}
		},
		BONGOS(CustomSound.DECOR_INSTRUMENT_BONGO),

		// Launchpad -> bit
		;

		@Getter
		@Nullable
		final String sound;
		private static final List<Integer> cMScale = List.of(1, 3, 5, 6, 8, 10, 11, 13, 15, 17, 18, 20, 22, 23);

		InstrumentSound(CustomSound sound) {
			this.sound = sound.getPath();
		}

		InstrumentSound(Sound sound) {
			this.sound = sound.getKey().getKey();
		}

		double getPitch(double lastPitch) {
			return SoundUtils.getPitch(SoundUtils.randomStep());
		}
	}

	static {
		Nexus.registerListener(new InstrumentListener());
	}

	private static class InstrumentListener implements Listener {
		Map<Player, Double> noiseMap = new HashMap<>();

		@EventHandler
		public void on(DecorationInteractEvent event) {
			if (event.getPlayer().isSneaking())
				return;

			if (!(event.getDecoration().getConfig() instanceof NoiseMaker noiseMaker))
				return;

			InstrumentSound instrumentSound = noiseMaker.getInstrumentSound();
			if (instrumentSound == null)
				return;

			ItemStack tool = ItemUtils.getTool(event.getPlayer());
			if (Nullables.isNotNullOrAir(tool) && DyeStation.isMagicPaintbrush(tool))
				return;

			event.setCancelled(true);

			double lastPitch = noiseMap.getOrDefault(event.getPlayer(), 1.0);

			lastPitch = noiseMaker.playSound(event.getPlayer(), event.getDecoration().getOrigin(), instrumentSound, lastPitch);

			noiseMap.put(event.getPlayer(), lastPitch);
		}
	}


}
