package me.pugabyte.nexus.features.store.annotations;

import me.pugabyte.nexus.features.store.PackageConsumers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Consumers {
	Consumer[] value() default {};

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@Repeatable(value = Consumers.class)
	@interface Consumer {
		PackageConsumers value();
	}
}
