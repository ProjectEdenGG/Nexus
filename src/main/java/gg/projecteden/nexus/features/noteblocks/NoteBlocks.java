package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.noteblock.NoteBlockData;
import gg.projecteden.nexus.models.noteblock.NoteBlockTracker;
import gg.projecteden.nexus.models.noteblock.NoteBlockTrackerService;
import gg.projecteden.utils.Env;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.NotePlayEvent;

import java.util.UUID;

/*
	TODO:
		Custom Block Handling
			- Block playing/changing note blocks that aren't Piano 0
			- When breaking, drop that custom block item instead
 */
@Environments(Env.TEST)
public class NoteBlocks extends Feature {
	private static final NoteBlockTrackerService trackerService = new NoteBlockTrackerService();
	private static NoteBlockTracker tracker;

	@Override
	public void onStart() {
		new NoteBlocksListener();
	}

	public static NoteBlockData put(UUID uuid, Location location) {
		tracker = trackerService.fromWorld(location);
		NoteBlockData data = new NoteBlockData(uuid, location.getBlock());
		tracker.put(location, data);
		trackerService.save(tracker);

		return data;
	}

	public static NoteBlockData put(Player player, Location location) {
		return put(player.getUniqueId(), location);
	}

	public static void remove(Location location) {
		tracker = trackerService.fromWorld(location);
		tracker.remove(location);
		trackerService.save(tracker);
	}

	public static void changePitch(boolean sneaking, Location location, NoteBlockData data) {
		tracker = trackerService.fromWorld(location);

		if (!sneaking)
			data.incrementStep();
		else
			data.decrementStep();

		tracker.put(location, data);
		trackerService.save(tracker);

		new NotePlayEvent(location.getBlock(), data.getBlockInstrument(), data.getBlockNote()).callEvent();
	}

	public static void changeVolume(Player player, Location location, NoteBlockData data) {
		tracker = trackerService.fromWorld(location);

		// TODO?

		tracker.put(location, data);
		trackerService.save(tracker);

	}

	public static String customSound(String instrument) {
		return "minecraft:custom.noteblock." + instrument;
	}
}
