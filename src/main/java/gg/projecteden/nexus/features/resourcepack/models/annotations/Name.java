package gg.projecteden.nexus.features.resourcepack.models.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Name {
	String value();
}
