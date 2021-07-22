package gg.projecteden.nexus.framework.commands.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds extra description to a command which can be seen via external auto-generated command documentation.
 * This is reserved for information that may not need to be viewable in-game but should be documented somewhere.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DescriptionExtra {
	String value();

}
