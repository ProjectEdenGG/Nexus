package gg.projecteden.nexus.features.customblocks.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.CustomBlocks;
import gg.projecteden.nexus.features.customblocks.NoteBlockUtils;
import gg.projecteden.nexus.models.noteblock.NoteBlockData;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.NotePlayEvent;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.isCustomNoteBlock;


public class NoteBlocksListener implements Listener {
	public NoteBlocksListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onPlayNote(NotePlayEvent event) {
		event.setCancelled(true);

		if (!CustomBlocks.isCustomNoteBlock(event.getBlock()))
			return;

		NoteBlockUtils.play(event.getBlock());
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlockPlaced();
		Block above = block.getRelative(BlockFace.UP);

		if (CustomBlocks.isCustomNoteBlock(above))
			NoteBlockUtils.getData(above, true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		Block eventBlock = event.getBlock();
		Material material = eventBlock.getType();
		if (material == Material.NOTE_BLOCK) {
			reset(eventBlock);
			eventBlock.getState().update(true, false);
		}

		Block aboveBlock = eventBlock.getRelative(BlockFace.UP);
		if (aboveBlock.getType().equals(Material.NOTE_BLOCK)) {

			while (aboveBlock.getType() == Material.NOTE_BLOCK) {
				reset(aboveBlock);
				aboveBlock.getState().update(true, false);
				aboveBlock = aboveBlock.getRelative(BlockFace.UP);
			}
		}
	}

	private void reset(Block block) {
		org.bukkit.block.data.type.NoteBlock noteBlock = (org.bukkit.block.data.type.NoteBlock) block.getBlockData();
		Instrument instrument = noteBlock.getInstrument();
		Note note = noteBlock.getNote();
		boolean powered = false;

		if (isCustomNoteBlock(block)) {
			NoteBlockData data = NoteBlockUtils.getData(block);
			data.setPowered(noteBlock.isPowered());

			instrument = data.getBlockInstrument();
			note = data.getBlockNote();
			powered = noteBlock.isPowered();
		}

		noteBlock.setInstrument(instrument);
		noteBlock.setNote(note);
		noteBlock.setPowered(powered);
		block.setBlockData(noteBlock, false);
	}
}
