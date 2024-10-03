package gg.projecteden.nexus.framework.commands.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Hides a subcommand from tab-completion unless they have a required permission node
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TabCompleteIgnore {
	/**
	 * Permission node required to show this subcommand in tab-completion
	 * @return permission node or empty string
	 */
	String permission() default "";

}
