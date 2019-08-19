package me.pugabyte.bncore.models.commands.models.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
	String value();
	boolean absolute() default false;

}
