package gg.projecteden.nexus.models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
	Class<? extends MongoService<? extends PlayerOwnedObject>> value();
}
