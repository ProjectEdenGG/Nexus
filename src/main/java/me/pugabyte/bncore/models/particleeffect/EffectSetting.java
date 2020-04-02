package me.pugabyte.bncore.models.particleeffect;

import java.util.Arrays;
import java.util.List;

public enum EffectSetting {
	COLOR(EffectType.values()),
	RAINBOW(EffectType.values()),
	RADIUS(EffectType.values()),
	DENSITY(EffectType.CIRCLE, EffectType.CIRCLES, EffectType.BN_RINGS, EffectType.GROWING_STARS),
	DISCO_UP(EffectType.DISCO),
	DISCO_DOWN(EffectType.DISCO),
	DISCO_BOTH(EffectType.DISCO),
	DISCO_SLOW(EffectType.DISCO),
	DISCO_FAST(EffectType.DISCO),
	DISCO_LINE(EffectType.DISCO);

	List<EffectType> applicableEffects;

	EffectSetting(EffectType... applicableEffects) {
		this.applicableEffects = Arrays.asList(applicableEffects);
	}
}
