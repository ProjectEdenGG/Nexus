package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common;

import org.bukkit.Instrument;

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

	String breakSound() default "ui.button.click";

	String customBreakSound() default "custom.block.wood.break";

	String placeSound() default "ui.button.click";

	String customPlaceSound() default "custom.block.wood.place";

	String stepSound() default "ui.button.click";

	String customStepSound() default "custom.block.wood.step";

	String hitSound() default "ui.button.click";

	String customHitSound() default "custom.block.wood.hit";

	String fallSound() default "ui.button.click";

	String customFallSound() default "custom.block.wood.fall";
}
