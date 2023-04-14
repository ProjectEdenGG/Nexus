package gg.projecteden.nexus.framework.commandsv2.annotations.path;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Pulls up a confirmation menu when a user runs this command to ensure they wish to do so
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Confirm {
	/**
	 * Sets the title of the confirmation menu window
	 * @return window title
	 */
	String title() default "&4Are you sure?";
}
