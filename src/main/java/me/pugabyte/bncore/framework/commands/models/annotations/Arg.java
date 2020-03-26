package me.pugabyte.bncore.framework.commands.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Arg {
	String value() default "";
	// Use for Lists due to java erasure
	Class<?> type() default void.class;
	Class<?> tabCompleter() default void.class;
	int contextArg() default -1;
}
