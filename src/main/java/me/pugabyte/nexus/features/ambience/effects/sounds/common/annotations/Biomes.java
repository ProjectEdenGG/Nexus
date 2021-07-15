package me.pugabyte.nexus.features.ambience.effects.sounds.common.annotations;

import me.pugabyte.nexus.utils.BiomeTag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Biomes {
	BiomeTag[] value();
}
