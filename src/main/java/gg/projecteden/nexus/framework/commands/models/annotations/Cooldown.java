package gg.projecteden.nexus.framework.commands.models.annotations;

import gg.projecteden.utils.TimeUtils.TickTime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cooldown {
	TickTime value();
	double x() default 1;
	boolean global() default false;
	String bypass() default "";

}
