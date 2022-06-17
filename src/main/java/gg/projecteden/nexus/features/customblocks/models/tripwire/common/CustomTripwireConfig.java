package gg.projecteden.nexus.features.customblocks.models.tripwire.common;

import org.bukkit.Sound;

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

	Sound breakSound() default Sound.MUSIC_GAME;

	String customBreakSound() default "custom.block.stone.break";

	Sound placeSound() default Sound.MUSIC_GAME;

	String customPlaceSound() default "custom.block.stone.place";

	Sound stepSound() default Sound.MUSIC_GAME;

	String customStepSound() default "custom.block.stone.step";

	Sound hitSound() default Sound.MUSIC_GAME;

	String customHitSound() default "custom.block.stone.hit";

	Sound fallSound() default Sound.MUSIC_GAME;

	String customFallSound() default "custom.block.stone.fall";
}
