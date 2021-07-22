package gg.projecteden.nexus.features.minigames.models.annotations;

import gg.projecteden.nexus.features.minigames.models.mechanics.Mechanic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MatchDataFor {
	Class<? extends Mechanic>[] value();
}
