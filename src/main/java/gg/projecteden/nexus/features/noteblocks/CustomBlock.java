package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.nexus.features.noteblocks.blocks.AppleCrate;
import gg.projecteden.nexus.features.noteblocks.blocks.NoteBlock;
import gg.projecteden.nexus.features.noteblocks.blocks.SugarCaneBundle;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public enum CustomBlock {
	NOTE_BLOCK(NoteBlock.class),
	APPLE_CRATE(AppleCrate.class),
	SUGAR_CANE_BUNDLE(SugarCaneBundle.class),
	;

	private ICustomBlock customBlock;

	CustomBlock(Class<? extends ICustomBlock> clazz) {
		customBlock = CustomBlocks.get(clazz);
	}

	public ICustomBlock get() {
		return customBlock;
	}
}
