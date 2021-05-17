package me.pugabyte.nexus.framework.commands.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies what object(s) this method converts to from a supplied string and optional contextual arguments
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConverterFor {
	/**
	 * Specifies what object(s) this method converts to from a supplied string and optional contextual arguments
	 * @return objects this method handles conversion for
	 */
	Class<?>[] value();

}
