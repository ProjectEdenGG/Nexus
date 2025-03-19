package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.misc;

import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.NoteBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.events.NoteBlockChangePitchEvent;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CustomBlockConfig(
	name = "Note Block",
	itemModel = ItemModelType.NOTE_BLOCK
)
@CustomNoteBlockConfig(
	instrument = Instrument.PIANO,
	step = 0
)
public class NoteBlock implements ICustomNoteBlock {

	@Override
	public @NonNull ItemBuilder getItemBuilder() {
		return new ItemBuilder(Material.NOTE_BLOCK);
	}

	@Override
	public double getBlockHardness() {
		return 0.8;
	}

	@Override
	public boolean requiresCorrectToolForDrops() {
		return false;
	}

	@Override
	public Material getMinimumPreferredTool() {
		return Material.WOODEN_AXE;
	}

	@Override
	public boolean onRightClickedWithItem(Player player, CustomBlock customBlock, Block block, ItemStack itemInHand) {
		if (player.isSneaking())
			return false;

		return onRightClickedWithoutItem(player, customBlock, block);
	}

	@Override
	public boolean onRightClickedWithoutItem(Player player, CustomBlock customBlock, Block block) {
		CustomBlockUtils.debug(player, "&e- is changing pitch");
		Location location = block.getLocation();
		NoteBlockData data = new NoteBlockData(block);
		NoteBlockChangePitchEvent event = new NoteBlockChangePitchEvent(player, location.getBlock());
		if (event.callEvent()) {
			NoteBlockUtils.changePitch(player, player.isSneaking(), location, data);
			CustomBlockUtils.debug(player, "&a<- changed pitch");
			return true;
		}

		return false;
	}

	@Override
	public boolean onLeftClickedWithItem(Player player, CustomBlock customBlock, Block block, ItemStack itemInHand) {
		return onLeftClickedWithoutItem(player, customBlock, block);
	}

	@Override
	public boolean onLeftClickedWithoutItem(Player player, CustomBlock customBlock, Block block) {
		org.bukkit.block.data.type.NoteBlock noteBlock = (org.bukkit.block.data.type.NoteBlock) block.getBlockData();
		CustomBlockUtils.debug(player, "&e<- is playing note");
		NoteBlockUtils.play(noteBlock, block.getLocation(), true, player);
		return true;
	}
}
