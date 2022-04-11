package gg.projecteden.nexus.features.customblocks;

import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomBlockTracker;
import gg.projecteden.nexus.models.customblock.CustomBlockTrackerService;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.utils.UUIDUtils;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;

public class CustomBlockUtils {
	private static final CustomBlockTrackerService trackerService = new CustomBlockTrackerService();
	private static CustomBlockTracker tracker;

	public static void placeBlockDatabase(UUID uuid, CustomBlock customBlock, Location location, BlockFace facing) {
		tracker = trackerService.fromWorld(location);
		CustomBlockData data = new CustomBlockData(uuid, customBlock.get().getCustomModelData(), facing);
		if (customBlock.equals(CustomBlock.NOTE_BLOCK))
			data.setNoteBlockData(new NoteBlockData(location.getBlock()));

		tracker.put(location, data);
		trackerService.save(tracker);
	}

	public static void breakBlockDatabase(Location location) {
		tracker = trackerService.fromWorld(location);
		CustomBlockData data = tracker.get(location);
		if (!data.exists())
			return;

		tracker.remove(location);
		trackerService.save(tracker);
	}

	public static @Nullable CustomBlockData getData(@NonNull NoteBlock noteBlock, Location location) {
		return getData(noteBlock, location, true);
	}

	public static @Nullable CustomBlockData getData(@NonNull NoteBlock noteBlock, Location location, boolean create) {
		tracker = trackerService.fromWorld(location);
		CustomBlockData data = tracker.get(location);
		if (!data.exists()) {
			if (!create)
				return null;

			debug("No custom block data exists for that location, creating");
			CustomBlock customBlock = CustomBlock.fromNoteBlock(noteBlock);
			if (customBlock == null) {
				debug("GetData: CustomBlock == null");
				return null;
			}

			placeBlockDatabase(UUIDUtils.UUID0, customBlock, location, BlockFace.UP);
			trackerService.save(tracker);
			return getData(noteBlock, location);
		}

		return data;
	}
}
