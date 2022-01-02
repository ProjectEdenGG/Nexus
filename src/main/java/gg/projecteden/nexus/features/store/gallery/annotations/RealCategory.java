package gg.projecteden.nexus.features.store.gallery.annotations;

import gg.projecteden.nexus.features.store.annotations.Category;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RealCategory {
	Category.StoreCategory value();

}
