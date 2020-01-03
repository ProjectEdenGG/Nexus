package me.pugabyte.bncore.framework.commands.models.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConverterFor {
	Class<?>[] value();

}
