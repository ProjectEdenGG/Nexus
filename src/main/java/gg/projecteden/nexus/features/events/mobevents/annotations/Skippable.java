package gg.projecteden.nexus.features.events.mobevents.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Skippable {
	boolean value();

	int sleepPercent() default 50;
}
