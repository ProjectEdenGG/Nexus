package me.pugabyte.nexus.features.ambience.managers;

import lombok.Getter;
import me.pugabyte.nexus.features.ambience.effects.sounds.common.Sound;
import me.pugabyte.nexus.features.ambience.effects.sounds.common.birds.BirdSoundEffectConfig;
import me.pugabyte.nexus.features.ambience.effects.sounds.common.birds.BirdSoundEffectType;
import me.pugabyte.nexus.features.ambience.effects.sounds.common.general.SoundEffectConfig;
import me.pugabyte.nexus.features.ambience.effects.sounds.common.general.SoundEffectType;
import me.pugabyte.nexus.features.ambience.managers.common.AmbienceManager;
import me.pugabyte.nexus.models.ambience.AmbienceUser;

import java.util.HashSet;
import java.util.Set;

public class SoundEffectManager extends AmbienceManager {
	@Getter
	private final Set<SoundEffectConfig> effects = new HashSet<>();

	@Override
	public void tick() {

	}

	@Override
	public void init(AmbienceUser user) {
		for (SoundEffectConfig effect : effects)
			effect.init(user);
	}

	@Override
	public void update(AmbienceUser user) {
		for (SoundEffectConfig effect : effects)
			effect.update(user);
	}

	@Override
	public void onStart() {
		effects.add(
			new SoundEffectConfig(
				SoundEffectType.BIRD_FOREST,
				new Sound("minecraft:custom.ambient.birds.woodpecker_1").pitchMin(0.9).pitchMax(110),
				15, 60));

		effects.add(
			new BirdSoundEffectConfig(
				BirdSoundEffectType.FOREST,
				15, 60));
	}
}
