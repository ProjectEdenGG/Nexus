package gg.projecteden.nexus.features.ambience.managers;

import gg.projecteden.nexus.features.ambience.effects.birds.common.BirdEffectConfig;
import gg.projecteden.nexus.features.ambience.effects.birds.common.BirdEffectType;
import gg.projecteden.nexus.features.ambience.managers.common.AmbienceManager;
import gg.projecteden.nexus.models.ambience.AmbienceUser;
import lombok.Getter;

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
