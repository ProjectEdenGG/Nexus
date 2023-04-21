package gg.projecteden.nexus.features.resourcepack.decoration.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
	Is used internally to properly determine interactions

	Automatically sets:
		rotatable = false
		rotationSnap = Degree.90

	- Decorations that are multiblock only in the Y plane should be fine without this annotation.
	- Should be applied to any decorations that are placed upon walls, regardless of how small.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiBlock {
}
