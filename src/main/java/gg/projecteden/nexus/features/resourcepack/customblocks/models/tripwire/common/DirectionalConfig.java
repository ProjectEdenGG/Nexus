package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DirectionalConfig {
	boolean north_EW();

	boolean east_EW();

	boolean south_EW();

	boolean west_EW();

	boolean attached_EW();

	boolean disarmed_EW();

	boolean powered_EW();

	boolean ignorePowered() default false;
}
