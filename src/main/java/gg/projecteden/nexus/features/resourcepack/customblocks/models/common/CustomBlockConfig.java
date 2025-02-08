package gg.projecteden.nexus.features.resourcepack.customblocks.models.common;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICustomBlock.PistonPushAction;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomBlockConfig {
	String name();

	CustomMaterial material();

	PistonPushAction getPistonPushedAction() default PistonPushAction.MOVE;
}
