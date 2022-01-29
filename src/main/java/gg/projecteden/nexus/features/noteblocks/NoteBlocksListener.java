package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.noteblock.NoteBlockTrackerService;
import gg.projecteden.nexus.utils.Nullables;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/*
	TODO:
		- allow placement of "real" noteblocks, maybe throw BlockPlaceEvent so CoreProtect logs it
 */
public class NoteBlocksListener implements Listener {
	NoteBlockTrackerService noteBlockTracker = new NoteBlockTrackerService();

	public NoteBlocksListener() {
		Nexus.registerListener(this);
	}

	// on change note block pitch
	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand()))
			return;

		if (!Action.RIGHT_CLICK_BLOCK.equals(event.getAction()))
			return;

		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block))
			return;

		if (!(block.getState() instanceof NoteBlock))
			return;

		event.setCancelled(true);
	}

	// on player interaction or redstone
	@EventHandler
	public void on(NotePlayEvent event) {
		Block block = event.getBlock();
		Location location = block.getLocation();

		NoteBlockData data = noteBlockTracker.getNoteBlockData(location);
		if (data == null)
			return;

		event.setCancelled(true);

		data.play(location);
	}

	private Note getPreviousNote(Note note) {
		int id = note.getId() - 1;
		return new Note(id < 0 ? 24 : id);
	}
}
