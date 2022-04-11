package gg.projecteden.nexus.features.customblocks;

import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomBlockTracker;
import gg.projecteden.nexus.models.customblock.CustomBlockTrackerService;
import gg.projecteden.utils.UUIDUtils;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;

public class CustomBlockUtils {
	private static final CustomBlockTrackerService customBlockTrackerService = new CustomBlockTrackerService();
	private static CustomBlockTracker customBlockTracker;

	public static void placeBlockDatabase(UUID uuid, CustomBlock customBlock, Location location) {
		if (customBlock.equals(CustomBlock.NOTE_BLOCK)) {
			NoteBlockUtils.placeBlockDatabase(uuid, location);
			return;
		}

		customBlockTracker = customBlockTrackerService.fromWorld(location);
		CustomBlockData data = new CustomBlockData(uuid, customBlock);

		customBlockTracker.put(location, data);
		customBlockTrackerService.save(customBlockTracker);
	}

	public static void breakBlock(CustomBlock customBlock, Location location) {
		if (customBlock.equals(CustomBlock.NOTE_BLOCK)) {
			NoteBlockUtils.breakBlock(location);
			return;
		}

		customBlockTracker = customBlockTrackerService.fromWorld(location);
		CustomBlockData data = customBlockTracker.get(location);
		if (!data.exists())
			return;

		customBlockTracker.remove(location);
		customBlockTrackerService.save(customBlockTracker);
	}

	public static @Nullable CustomBlockData getData(@NonNull NoteBlock noteBlock, Location location) {
		return getData(noteBlock, location, true);
	}

	public static @Nullable CustomBlockData getData(@NonNull NoteBlock noteBlock, Location location, boolean create) {
		customBlockTracker = customBlockTrackerService.fromWorld(location);
		CustomBlockData data = customBlockTracker.get(location);
		if (!data.exists()) {
			if (!create)
				return null;

			debug("No custom block data exists for that location, creating");
			CustomBlock customBlock = CustomBlock.fromNoteBlock(noteBlock);
			if (customBlock == null) {
				debug("GetData: CustomBlock == null");
				return null;
			}

			placeBlockDatabase(UUIDUtils.UUID0, customBlock, location);
			customBlockTrackerService.save(customBlockTracker);
			return getData(noteBlock, location);
		}

		return data;
	}
}
