package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.utils.Env;
import org.bukkit.Material;
import org.bukkit.block.Block;

/*
	TODO:
		fix placing & breaking
 */
@Environments(Env.TEST)
public class CustomBlocks extends Feature {
	@Override
	public void onStart() {
		new CustomBlocksListener();
	}

	public static boolean isCustom(Block block) {
		if (Nullables.isNullOrAir(block))
			return false;

		return block.getType().equals(Material.NOTE_BLOCK);
	}

	public static boolean isCustomNoteBlock(Block block) {
		if (!isCustom(block))
			return false;

		CustomBlock customBlock = CustomBlock.fromNoteBlock(block);
		if (customBlock == null)
			return false;

		return CustomBlock.NOTE_BLOCK.equals(customBlock);
	}
}
