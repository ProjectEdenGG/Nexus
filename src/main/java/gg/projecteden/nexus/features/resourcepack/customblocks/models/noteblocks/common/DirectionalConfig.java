package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DirectionalConfig {
	int step_NS();
	int step_EW();
}
