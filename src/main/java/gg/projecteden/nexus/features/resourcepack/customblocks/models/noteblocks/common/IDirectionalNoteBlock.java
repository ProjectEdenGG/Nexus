package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.IDirectional;

public interface IDirectionalNoteBlock extends ICustomNoteBlock, IDirectional {
	default DirectionalConfig getDirectionalConfig() {
		return getClass().getAnnotation(DirectionalConfig.class);
	}

	default int getNoteBlockStep_NS(){
		return getDirectionalConfig().step_NS();
	}

	default int getNoteBlockStep_EW(){
		return getDirectionalConfig().step_EW();
	}
}
