package gg.projecteden.nexus.features.mobheads.common;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HeadConfig {

	String headId() default "";

	Material headType() default Material.AIR;

	EntityType entityType();

	Class<? extends MobHeadVariant> variantClass() default MobHeadVariant.class;

}
