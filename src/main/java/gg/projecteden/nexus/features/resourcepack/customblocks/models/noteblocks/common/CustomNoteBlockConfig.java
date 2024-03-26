package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common;

import org.bukkit.Instrument;
import org.bukkit.Sound;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomNoteBlockConfig {
	Instrument instrument();

	int step();

	boolean powered() default false;

	Sound breakSound() default Sound.UI_BUTTON_CLICK;

	String customBreakSound() default "custom.block.wood.break";

	Sound placeSound() default Sound.UI_BUTTON_CLICK;

	String customPlaceSound() default "custom.block.wood.place";

	Sound stepSound() default Sound.UI_BUTTON_CLICK;

	String customStepSound() default "custom.block.wood.step";

	Sound hitSound() default Sound.UI_BUTTON_CLICK;

	String customHitSound() default "custom.block.wood.hit";

	Sound fallSound() default Sound.UI_BUTTON_CLICK;

	String customFallSound() default "custom.block.wood.fall";
}
