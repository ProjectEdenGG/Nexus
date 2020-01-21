package me.pugabyte.bncore.features.minigames.models.annotations;

import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MatchDataFor {
	Class<? extends Mechanic> value();
}
