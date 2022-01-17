package gg.projecteden.nexus.features.minigames.menus.annotations;

import gg.projecteden.nexus.features.minigames.models.mechanics.Mechanic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CustomMechanicSettings {
	Class<? extends Mechanic>[] value();
}
