package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Theme;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeConfig {
	boolean unbuyable() default false;

	double price() default -1;

	int tokens() default -1;

	Theme theme() default Theme.GENERAL;

	Catalog.Tab[] tabs() default {};
}
