package gg.projecteden.nexus.features.resourcepack.customblocks.listeners;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock.CustomBlockType;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.models.customblock.CustomNoteBlockTracker;
import gg.projecteden.nexus.models.customblock.CustomNoteBlockTrackerService;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Chunk;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;

import java.util.List;

@Environments(Env.TEST)
public class ConversionListener implements Listener {
	private static final CustomNoteBlockTrackerService trackerService = new CustomNoteBlockTrackerService();

	public ConversionListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onGenerate(ChunkPopulateEvent event) {
		convertCustomBlocks(event.getChunk());
	}

	@EventHandler
	public void on(ChunkLoadEvent event) {
		convertCustomBlocks(event.getChunk());
	}

	public static void convertCustomBlocks(Chunk chunk) {
		convertCustomBlocks(chunk, false);
	}

	public static void convertCustomBlocks(Chunk chunk, boolean override) {
		long key = chunk.getChunkKey();
		CustomNoteBlockTracker tracker = trackerService.fromWorld(chunk.getWorld());
		if (!override && tracker.getConvertedChunkKeys().contains(key))
			return;

		tracker.getConvertedChunkKeys().add(key);
		trackerService.save(tracker);

		if (!WorldUtils.isInWorldBorder(chunk.getWorld(), chunk))
			return;

		List<Location> customBlockList = BlockUtils.getBlocksInChunk(chunk, blockData -> CustomBlockType.getBlockMaterials().contains(blockData.getMaterial()));
		if (customBlockList.isEmpty())
			return;

		for (Location location : customBlockList) {
			Block block = location.getBlock();
			Material material = block.getType();
			CustomBlock customBlock = CustomBlock.from(block);
			final Block below = block.getRelative(BlockFace.DOWN);
			switch (material) {
				case NOTE_BLOCK -> {
					String logMessage;
					// Assume Staff and Minigames worlds are real custom blocks
					boolean assumeCustomBlock = WorldGroup.MINIGAMES.contains(location) || WorldGroup.STAFF.contains(location);
					if (assumeCustomBlock && customBlock != null) {
						block.setBlockData(customBlock.get().getBlockData(BlockFace.UP, below), false);
						logMessage = "Creating CustomBlock " + StringUtils.camelCase(customBlock) + " " + StringUtils.getShortLocationString(location);
					} else {
						NoteBlock noteBlockData = (NoteBlock) block.getBlockData();
						noteBlockData.setInstrument(Instrument.PIANO);
						block.setBlockData(noteBlockData, false);
						logMessage = "Creating CustomBlock NoteBlock at " + StringUtils.getShortLocationString(location);
					}

					CustomBlockUtils.broadcastDebug(logMessage);
					IOUtils.fileAppend("customblocks", logMessage);
				}
				case TRIPWIRE -> {
					// TODO: Disable tripwire customblocks
					if (ICustomTripwire.isNotEnabled())
						return;
					//

					MultipleFacing multipleFacing = (MultipleFacing) block.getBlockData();
					BlockFace facing = BlockFace.NORTH;
					if (multipleFacing.getFaces().contains(BlockFace.EAST))
						facing = BlockFace.EAST;

					block.setBlockData(CustomBlock.TRIPWIRE.get().getBlockData(facing, below), false);

					String logMessage = "Creating CustomBlock TripwireData at " + StringUtils.getShortLocationString(location);

					CustomBlockUtils.broadcastDebug(logMessage);
					IOUtils.fileAppend("customblocks", logMessage);
				}
			}
		}
	}
}
