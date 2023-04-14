package gg.projecteden.nexus.framework.commandsv2.annotations.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Redirects {
	Redirect[] value() default {};

	/**
	 * Registers psuedo-aliases for a command by detecting commands specified in {@link #from()} and replacing them
	 * with {@link #to()}. This will preserve arguments supplied after the {@link #from()}.
	 * <p>
	 * n.b. "commands" specified in {@link #from()} will not appear in tab-completion, nor will they be available to
	 * console, logged, etc.
	 */
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Repeatable(value = Redirects.class)
	@interface Redirect {
		/**
		 * List of commands to redirect from
		 * @return commands to redirect, including the <code>/</code>
		 */
		String[] from();
		/**
		 * Command to redirect to
		 * @return command to redirect to, including the <code>/</code>
		 */
		String to();
	}
}
