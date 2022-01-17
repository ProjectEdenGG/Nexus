package gg.projecteden.nexus.features.store.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpirationCommands {
	ExpirationCommand[] value() default {};

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@Repeatable(value = ExpirationCommands.class)
	@interface ExpirationCommand {
		String value();
	}
}
