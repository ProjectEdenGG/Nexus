package gg.projecteden.nexus.features.customblocks;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.utils.Env;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;

import java.util.List;

/*
	TODO:
		- Appropriate tool & mining speed --> CustomBlockBreaking
		- Sounds --> Testing
		- //
		- Known issues:
			- Custom blocks may flash when placing blocks near them (clientside only) --> Titan
			- Players arm will swing on interact w/ custom blocks --> Titan
 */

@Environments(Env.TEST)
public class CustomBlocks extends Feature {
	@Override
	public void onStart() {
		new CustomBlocksListener();
	}

	public static void debug(String message) {
		List<Dev> devs = List.of(Dev.WAKKA, Dev.GRIFFIN);
		for (Dev dev : devs) {
			if (dev.isOnline())
				dev.send(message);
		}
	}

	public static boolean isCustom(Block block) {
		if (Nullables.isNullOrAir(block))
			return false;

		if (!block.getType().equals(Material.NOTE_BLOCK))
			return false;

		NoteBlock noteBlock = (NoteBlock) block.getBlockData();
		if (CustomBlock.fromBlockData(noteBlock) == null)
			return false;

		return true;
	}

	public static boolean isCustomNoteBlock(Block block) {
		if (!isCustom(block))
			return false;

		NoteBlock noteBlock = (NoteBlock) block.getBlockData();
		CustomBlock customBlock = CustomBlock.fromBlockData(noteBlock);
		if (customBlock == null) {
			debug("isCustomNoteBlock: CustomBlock == null");
			return false;
		}

		return CustomBlock.NOTE_BLOCK.equals(customBlock);
	}
}
