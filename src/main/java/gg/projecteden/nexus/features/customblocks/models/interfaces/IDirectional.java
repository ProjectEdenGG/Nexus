package gg.projecteden.nexus.features.customblocks.models.interfaces;

import lombok.NonNull;
import org.bukkit.Instrument;

public interface IDirectional {
	@NonNull Instrument getNoteBlockInstrument_NS();

	int getNoteBlockStep_NS();

	@NonNull Instrument getNoteBlockInstrument_EW();

	int getNoteBlockStep_EW();
}
