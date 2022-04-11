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

		NoteBlock noteBlock = (NoteBlock) block.getBlockData();
		CustomBlock customBlock = CustomBlock.fromNoteBlock(noteBlock);
		if (customBlock == null) {
			debug("isCustomNoteBlock: CustomBlock == null");
			return false;
		}

		return CustomBlock.NOTE_BLOCK.equals(customBlock);
	}
}
