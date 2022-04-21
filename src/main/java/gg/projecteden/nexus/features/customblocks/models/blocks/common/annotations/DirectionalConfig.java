package gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations;

import gg.projecteden.nexus.features.customblocks.models.NoteBlockInstrument;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DirectionalConfig {
	NoteBlockInstrument instrument_NS() default NoteBlockInstrument.NONE;
	int step_NS();
	NoteBlockInstrument instrument_EW() default NoteBlockInstrument.NONE;
	int step_EW();
}
