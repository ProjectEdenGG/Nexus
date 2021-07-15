package me.pugabyte.nexus.features.ambience.managers;

import lombok.Getter;
import me.pugabyte.nexus.features.ambience.effects.birds.common.BirdEffectConfig;
import me.pugabyte.nexus.features.ambience.effects.birds.common.BirdEffectType;
import me.pugabyte.nexus.features.ambience.managers.common.AmbienceManager;
import me.pugabyte.nexus.models.ambience.AmbienceUser;

import java.util.HashSet;
import java.util.Set;

public class BirdEffectManager extends AmbienceManager {
	@Getter
	private final Set<BirdEffectConfig> effects = new HashSet<>();

	@Override
	public void tick() {
	}

	@Override
	public void init(AmbienceUser user) {
		for (BirdEffectConfig effect : effects)
			effect.init(user);
	}

	@Override
	public void update(AmbienceUser user) {
		if (!user.isSounds())
			return;

		for (BirdEffectConfig effect : effects)
			effect.update(user);
	}

	@Override
	public void onStart() {
		effects.add(new BirdEffectConfig(BirdEffectType.FOREST, 15, 60));
	}

}
