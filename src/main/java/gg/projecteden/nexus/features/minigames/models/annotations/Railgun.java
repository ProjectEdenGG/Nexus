package gg.projecteden.nexus.features.minigames.models.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Railgun {
	int cooldownTicks() default 24;

	boolean damageWithConsole() default false;

	boolean mustBeGliding() default false;
}
