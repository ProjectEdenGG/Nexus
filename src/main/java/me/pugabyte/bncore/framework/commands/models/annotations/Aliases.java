package me.pugabyte.bncore.framework.commands.models.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// TODO: Validation [a-zA-Z0-9]
@Retention(RetentionPolicy.RUNTIME)
public @interface Aliases {
	String[] value();

}
