package gg.projecteden.nexus.features.store.annotations;

import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Display {
	Material value() default Material.AIR;
	ItemModelType model() default ItemModelType.INVISIBLE;
	int amount() default 1;

}
