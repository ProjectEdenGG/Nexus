package gg.projecteden.nexus.features.customblocks;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.features.customblocks.listeners.CustomBlocksListener;
import gg.projecteden.nexus.features.customblocks.listeners.NoteBlocksListener;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.utils.Env;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

/*
	TODO:
		- When placing a noteblock ontop of another noteblock, the above changes instrument, and the below increases pitch
		- Sounds: Wait until SoundEvents are fixed
		- Add support for sideways placement
		- Add & register recipes
		- appropriate tool/mining speed/block hardness: item digspeed attributes or potion eggects
		- creative pick block
 */
@Environments(Env.TEST)
public class CustomBlocks extends Feature {
	@Override
	public void onStart() {
		new CustomBlocksListener();
		new NoteBlocksListener();
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
