package me.pugabyte.nexus.framework.commands.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Switch {
	String value() default "";
	String permission() default "";
	char shorthand() default '-';

	// TODO int context() default -1;
	Class<?> tabCompleter() default void.class;

	// Use for Lists due to java erasure
	Class<?> type() default void.class;

	double min() default Short.MIN_VALUE;
	double max() default Short.MAX_VALUE;
	String regex() default "";

}
