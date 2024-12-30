package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomTripwireConfig {

	boolean north_NS();

	boolean east_NS();

	boolean south_NS();

	boolean west_NS();

	boolean attached_NS();

	boolean disarmed_NS();

	boolean powered_NS();

	boolean ignorePowered() default false;

	String breakSound() default "ui.button.click";

	String customBreakSound() default "custom.block.stone.break";

	String placeSound() default "ui.button.click";

	String customPlaceSound() default "custom.block.stone.place";

	String stepSound() default "ui.button.click";

	String customStepSound() default "custom.block.stone.step";

	String hitSound() default "ui.button.click";

	String customHitSound() default "custom.block.stone.hit";

	String fallSound() default "ui.button.click";

	String customFallSound() default "custom.block.stone.fall";
}
