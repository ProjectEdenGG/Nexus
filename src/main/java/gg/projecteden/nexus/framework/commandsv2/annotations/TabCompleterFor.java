package gg.projecteden.nexus.framework.commandsv2.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets what class(es) this method provides a tab completer for
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TabCompleterFor {
	/**
	 * Sets what class(es) this method provides a tab completer for
	 * @return classes to tab complete
	 */
	Class<?>[] value();

}
