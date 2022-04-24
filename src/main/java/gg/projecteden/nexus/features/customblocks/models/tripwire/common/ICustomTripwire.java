package gg.projecteden.nexus.features.customblocks.models.tripwire.common;

import gg.projecteden.nexus.features.customblocks.models.common.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;

public interface ICustomTripwire extends ICustomBlock {
	@Override
	default Material getBlockMaterial() {
		return Material.TRIPWIRE;
	}

	private CustomTripwireConfig getConfig() {
		return getClass().getAnnotation(CustomTripwireConfig.class);
	}

	// Sounds
	default @NonNull String getBreakSound() {
		Sound sound = getConfig().breakSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customBreakSound();
		}

		return customSound;
	}

	default @NonNull String getPlaceSound() {
		Sound sound = getConfig().placeSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customPlaceSound();
		}

		return customSound;
	}

	default @NonNull String getStepSound() {
		Sound sound = getConfig().stepSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customStepSound();
		}

		return customSound;
	}

	default @NonNull String getHitSound() {
		Sound sound = getConfig().hitSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customHitSound();
		}

		return customSound;
	}

	default @NonNull String getFallSound() {
		Sound sound = getConfig().fallSound();
		String customSound = sound.getKey().getKey();
		if (sound.equals(Sound.MUSIC_GAME)) {
			customSound = getConfig().customFallSound();
		}

		return customSound;
	}

}
