package me.pugabyte.nexus.framework.commands.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Redirects {
	Redirect[] value() default {};

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Repeatable(value = Redirects.class)
	@interface Redirect {
		String[] from();
		String to();
	}
}
