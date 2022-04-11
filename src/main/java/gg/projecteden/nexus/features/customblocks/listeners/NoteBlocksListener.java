package gg.projecteden.nexus.features.customblocks.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.CustomBlocks;
import gg.projecteden.nexus.features.customblocks.NoteBlockUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.NotePlayEvent;


public class NoteBlocksListener implements Listener {
	public NoteBlocksListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onPlayNote(NotePlayEvent event) {
		event.setCancelled(true);

		if (!CustomBlocks.isCustomNoteBlock(event.getBlock()))
			return;

		NoteBlockUtils.play(event.getBlock(), true);
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
}
