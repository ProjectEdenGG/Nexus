package gg.projecteden.nexus.features.events.mobevents.annotations;

import gg.projecteden.utils.TimeUtils.TickTime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Duration {
	TickTime value();

	double x() default 1;

}
