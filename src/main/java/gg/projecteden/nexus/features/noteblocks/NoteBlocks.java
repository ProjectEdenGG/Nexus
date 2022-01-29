package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.utils.Env;

@Environments(Env.DEV)
public class NoteBlocks extends Feature {
	@Override
	public void onStart() {
		new NoteBlocksListener();
	}
}
