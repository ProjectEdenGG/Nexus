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
		- remove note block item, use only paper instead, replace recipe/change result item in precraft
		- allow crafting of custom blocks that are used in recipes
		- Appropriate tool & mining speed --> CustomBlockBreaking
		- Sounds: Wait until SoundEvents are fixed
		- Bug: if looking at a custom block, and you move a note block around in your hotbar, it will change the note block to that custom block type
		- //
		- Known issues:
			- CreativePickBlock on a custom block while a note block is in your inventory will always select the note block, and doesn't throw InventoryClickEvent or CreativeClickEvent
				- get rid of the note block item, and replace it with a paper model
			- Custom blocks may flash when placing blocks near them (clientside only)
			- Players arm will swing on interact w/ custom blocks
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
		if (CustomBlock.fromNoteBlock(noteBlock) == null)
			return false;

		return true;
	}

	public static boolean isCustomNoteBlock(Block block) {
		if (!isCustom(block))
			return false;

		NoteBlock noteBlock = (NoteBlock) block.getBlockData();
		CustomBlock customBlock = CustomBlock.fromNoteBlock(noteBlock);
		if (customBlock == null) {
			debug("isCustomNoteBlock: CustomBlock == null");
			return false;
		}

		return CustomBlock.NOTE_BLOCK.equals(customBlock);
	}
}
