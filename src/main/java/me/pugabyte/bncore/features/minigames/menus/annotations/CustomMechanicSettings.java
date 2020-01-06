package me.pugabyte.bncore.features.minigames.menus.annotations;

import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CustomMechanicSettings {
    MechanicType value();
}
