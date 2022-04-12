package gg.projecteden.nexus.features.customblocks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.ICustomBlock;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomBlockTracker;
import gg.projecteden.nexus.models.customblock.CustomBlockTrackerService;
import gg.projecteden.nexus.models.customblock.NoteBlockData;
import gg.projecteden.utils.UUIDUtils;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.features.customblocks.CustomBlocks.debug;

public class CustomBlockUtils {
	private static final CustomBlockTrackerService trackerService = new CustomBlockTrackerService();
	private static CustomBlockTracker tracker;

	public static CustomBlockData placeBlockDatabase(UUID uuid, CustomBlock customBlock, Location location, BlockFace facing) {
		tracker = trackerService.fromWorld(location);
		CustomBlockData data = new CustomBlockData(uuid, customBlock.get().getCustomModelData(), facing);
		if (customBlock.equals(CustomBlock.NOTE_BLOCK))
			data.setNoteBlockData(new NoteBlockData(location.getBlock()));

		tracker.put(location, data);
		trackerService.save(tracker);
		return data;
	}

	public static void breakBlockDatabase(Location location) {
		tracker = trackerService.fromWorld(location);
		CustomBlockData data = tracker.get(location);
		if (!data.exists())
			return;

		tracker.remove(location);
		trackerService.save(tracker);
	}

	public static void pistonMove(Block piston, Map<CustomBlockData, Pair<Location, Location>> blocksToMove) {
		if (blocksToMove.isEmpty())
			return;

		tracker = trackerService.fromWorld(piston.getWorld());
		Map<CustomBlockData, Location> currentLocMap = new HashMap<>();
		Map<CustomBlockData, Location> newLocMap = new HashMap<>();

		for (CustomBlockData data : blocksToMove.keySet()) {
			currentLocMap.put(data, blocksToMove.get(data).getFirst());
			newLocMap.put(data, blocksToMove.get(data).getSecond());
		}

//		debug("--- Start");

		// remove
		for (CustomBlockData data : currentLocMap.keySet()) {
			Location currentLocation = currentLocMap.get(data);
			tracker.remove(currentLocation);

//			debug("PistonEvent: " + data.getCustomBlock().name() + " | " + StringUtils.getCoordinateString(currentLocation));
		}

//		debug("---");

		// place
		for (CustomBlockData data : newLocMap.keySet()) {
			Location newLocation = newLocMap.get(data);
			tracker.put(newLocation, data);

//			debug("Moving Block: " + data.getCustomBlock().name() + " | " + StringUtils.getCoordinateString(newLocation));
		}

//		debug("--- End");

		trackerService.save(tracker);
	}

	public static @Nullable CustomBlockData getData(@NonNull NoteBlock noteBlock, Location location) {
		tracker = trackerService.fromWorld(location);
		CustomBlockData data = tracker.get(location);
		if (!data.exists()) {
			CustomBlock customBlock = CustomBlock.fromNoteBlock(noteBlock);
			if (customBlock == null) {
				debug("GetData: CustomBlock == null");
				return null;
			}

			debug("GetData: creating new data for " + customBlock.name());
			BlockFace facing = CustomBlockUtils.getFacing(customBlock, noteBlock);
			data = placeBlockDatabase(UUIDUtils.UUID0, customBlock, location, facing);
		}

		return data;
	}

	private static BlockFace getFacing(CustomBlock _customBlock, NoteBlock noteBlock) {
		ICustomBlock customBlock = _customBlock.get();
		if (!customBlock.canPlaceSideways())
			return BlockFace.UP;

		Instrument instrument = noteBlock.getInstrument();
		int step = noteBlock.getNote().getId();

		if (customBlock.getNoteBlockInstrument(BlockFace.NORTH) == instrument
			&& customBlock.getNoteBlockStep(BlockFace.NORTH) == step)
			return BlockFace.NORTH;

		return BlockFace.EAST;
	}
}
