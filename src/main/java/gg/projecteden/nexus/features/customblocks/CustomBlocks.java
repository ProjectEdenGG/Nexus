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
		- custom blocks database, proper conversions
		- (while sneaking only) When placing a noteblock ontop of another noteblock, the above changes instrument, and the below increases pitch
		- Add support for sideways placement
		- creative pick block -> switch active slot
		- //
		- Sounds: Wait until SoundEvents are fixed

		- appropriate tool/mining speed/block hardness: item digspeed attributes or potion eggects
		ItemMeta meta = tool.getItemMeta();
		UUID SLOW_DIG = UUID.fromString("55FCED67-E92A-486E-9800-B47F202C4386");
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
			new AttributeModifier(SLOW_DIG, "custom_block_mining_speed", 2, Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HAND));
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
