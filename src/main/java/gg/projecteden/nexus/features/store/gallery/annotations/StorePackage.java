package gg.projecteden.nexus.features.store.gallery.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StorePackage {
	gg.projecteden.nexus.features.store.Package value();

}
