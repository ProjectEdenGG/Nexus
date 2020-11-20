package me.pugabyte.nexus.features.minigames.menus.annotations;

import me.pugabyte.nexus.features.minigames.models.mechanics.Mechanic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CustomMechanicSettings {
	Class<? extends Mechanic>[] value();
}
