package gg.projecteden.nexus.features.customblocks.models.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomBlockConfig {
	String name();

	int modelId();

	boolean isPistonPushable() default true;
}
