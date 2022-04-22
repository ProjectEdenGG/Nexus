package gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations;

import org.bukkit.Instrument;
import org.bukkit.Sound;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomBlockConfig {
	String name();

	int modelId();

	Instrument instrument();

	int step();

	boolean isPistonPushable() default true;

	Sound breakSound() default Sound.MUSIC_GAME;

	String customBreakSound() default "custom.block.wood.break";

	Sound placeSound() default Sound.MUSIC_GAME;

	String customPlaceSound() default "custom.block.wood.place";

	Sound stepSound() default Sound.MUSIC_GAME;

	String customStepSound() default "custom.block.wood.step";

	Sound hitSound() default Sound.MUSIC_GAME;

	String customHitSound() default "custom.block.wood.hit";

	Sound fallSound() default Sound.MUSIC_GAME;

	String customFallSound() default "custom.block.wood.fall";
}
