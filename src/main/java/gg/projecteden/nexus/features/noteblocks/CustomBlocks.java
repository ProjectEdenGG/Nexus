package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.nexus.framework.features.Feature;
import me.lexikiq.event.sound.LocationNamedSoundEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomBlocks extends Feature implements Listener {

	@EventHandler
	public void onSoundEvent(LocationNamedSoundEvent event) {
		Block block = event.getLocation().getBlock();
		Block below = block.getRelative(BlockFace.DOWN);
		Block source;
		if (block.getType().equals(Material.NOTE_BLOCK))
			source = block;
		else if (below.getType().equals(Material.NOTE_BLOCK))
			source = below;
		else
			return;

		CustomBlock _customBlock = CustomBlock.fromNoteBlock((NoteBlock) source.getBlockData());
		if (_customBlock == null)
			return;

		ICustomBlock customBlock = _customBlock.get();
		String sound = event.getSound().getKey().getKey();
		if (event.getPlayer() != null)
			event.getPlayer().sendMessage("SoundEvent: " + customBlock.getName() + " - " + sound);
	}
}
