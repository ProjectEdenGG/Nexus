package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.noteblock.NoteBlockData;
import gg.projecteden.nexus.models.noteblock.NoteBlockTracker;
import gg.projecteden.nexus.models.noteblock.NoteBlockTrackerService;
import gg.projecteden.utils.Env;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

@Environments(Env.TEST)
public class NoteBlocks extends Feature {
	private static final NoteBlockTrackerService trackerService = new NoteBlockTrackerService();
	private static NoteBlockTracker tracker;

	@Override
	public void onStart() {
		new NoteBlocksListener();
	}

	public static void place(Player player, Block block) {
		Location location = block.getLocation();

		tracker = trackerService.get(location);
		NoteBlock noteBlock = (NoteBlock) block.getBlockData();

		// correct texture
		noteBlock.setInstrument(Instrument.PIANO);
		noteBlock.setNote(new Note(0));
		block.setBlockData(noteBlock);

		NoteBlockData data = new NoteBlockData(player, NoteBlockInstrument.getInstrument(block), 0);
		tracker.put(location, data);
		trackerService.save(tracker);
	}

	public static void remove(Location location) {
		tracker = trackerService.get(location);
		tracker.remove(location);
		trackerService.save(tracker);
	}

	public static void changePitch(boolean sneaking, Action action, Location location) {
		tracker = trackerService.get(location);
		NoteBlockData data = tracker.get(location);

		if (action.equals(Action.RIGHT_CLICK_BLOCK))
			data.incrementStep();
		else if (action.equals(Action.LEFT_CLICK_BLOCK) && sneaking)
			data.decrementStep();

		tracker.put(location, data);
		trackerService.save(tracker);

		data.play(location);
	}

	public static void changeVolume(Player player, Location location) {
		tracker = trackerService.get(location);
		// TODO
	}
}
