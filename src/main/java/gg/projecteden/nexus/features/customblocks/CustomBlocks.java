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
		- define all added blocks
		- allow crafting of custom blocks that are used in recipes
		- Appropriate tool & mining speed
			ItemMeta meta = tool.getItemMeta();
			UUID SLOW_DIG = UUID.fromString("55FCED67-E92A-486E-9800-B47F202C4386");
			meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
				new AttributeModifier(SLOW_DIG, "custom_block_mining_speed", 2, Operation.MULTIPLY_SCALAR_1, EquipmentSlot.HAND));
		- Sounds: Wait until SoundEvents are fixed
		- //
		- Known issues:
			- CreativePickBlock on a custom block while a note block is in your inventory will always select the note block, and doesn't throw InventoryClickEvent or CreativeClickEvent
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
