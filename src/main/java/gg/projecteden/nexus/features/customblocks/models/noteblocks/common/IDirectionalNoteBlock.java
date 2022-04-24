package gg.projecteden.nexus.features.customblocks.models.noteblocks.common;

import gg.projecteden.nexus.features.customblocks.models.NoteBlockInstrument;
import gg.projecteden.nexus.features.customblocks.models.common.IDirectional;
import lombok.NonNull;
import org.bukkit.Instrument;

public interface IDirectionalNoteBlock extends ICustomNoteBlock, IDirectional {
	default DirectionalConfig getDirectionalConfig() {
		return getClass().getAnnotation(DirectionalConfig.class);
	}

	default @NonNull Instrument getNoteBlockInstrument_NS() {
		NoteBlockInstrument instrument = getDirectionalConfig().instrument_NS();
		if (instrument == NoteBlockInstrument.NONE || instrument.isCustom())
			return getNoteBlockConfig().instrument();

		return instrument.getInstrument();
	}

	default int getNoteBlockStep_NS(){
		return getDirectionalConfig().step_NS();
	}

	default @NonNull Instrument getNoteBlockInstrument_EW(){
		NoteBlockInstrument instrument = getDirectionalConfig().instrument_EW();
		if(instrument == NoteBlockInstrument.NONE || instrument.isCustom())
			return getNoteBlockConfig().instrument();

		return instrument.getInstrument();
	}

	default int getNoteBlockStep_EW(){
		return getDirectionalConfig().step_EW();
	}
}
