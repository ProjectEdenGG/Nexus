package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common;

import org.bukkit.Sound;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WaterLoggedConfig {
	boolean north_NS();

	boolean south_NS();

	boolean east_NS();

	boolean west_NS();

	boolean attached_NS();

	boolean disarmed_NS();

	boolean powered_NS();

	boolean ignorePowered() default false;

	Sound breakSound() default Sound.UI_BUTTON_CLICK;

	String customBreakSound() default "custom.block.stone.break";

	Sound placeSound() default Sound.UI_BUTTON_CLICK;

	String customPlaceSound() default "custom.block.stone.place";

	Sound stepSound() default Sound.UI_BUTTON_CLICK;

	String customStepSound() default "custom.block.stone.step";

	Sound hitSound() default Sound.UI_BUTTON_CLICK;

	String customHitSound() default "custom.block.stone.hit";

	Sound fallSound() default Sound.UI_BUTTON_CLICK;

	String customFallSound() default "custom.block.stone.fall";
}
