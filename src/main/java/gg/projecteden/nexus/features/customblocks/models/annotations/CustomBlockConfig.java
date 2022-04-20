package gg.projecteden.nexus.features.customblocks.models.annotations;

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

	Sound breakSound() default Sound.BLOCK_WOOD_BREAK;
	Sound placeSound() default Sound.BLOCK_WOOD_PLACE;
	Sound stepSound() default Sound.BLOCK_WOOD_STEP;
	Sound hitSound() default Sound.BLOCK_WOOD_HIT;
}
