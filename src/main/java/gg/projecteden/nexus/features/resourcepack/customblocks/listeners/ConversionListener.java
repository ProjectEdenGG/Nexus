package gg.projecteden.nexus.features.resourcepack.customblocks.listeners;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocksLang;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock.CustomBlockType;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldUtils;
import lombok.NonNull;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Environments(Env.TEST)
public class ConversionListener implements Listener {
	private final Set<Long> convertedChunks = new HashSet<>();

	public ConversionListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onGenerate(ChunkPopulateEvent event) {
		long key = event.getChunk().getChunkKey();
		if (convertedChunks.contains(key))
			return;

		convertedChunks.add(key);
		convertCustomBlocks(event.getChunk());
	}

	@EventHandler
	public void on(ChunkLoadEvent event) {
		long key = event.getChunk().getChunkKey();
		if (convertedChunks.contains(key))
			return;

		convertedChunks.add(key);
		convertCustomBlocks(event.getChunk());
	}

	public static void convertCustomBlocks(Chunk chunk) {
		if (!WorldUtils.isInWorldBorder(chunk.getWorld(), chunk))
			return;

		List<Location> customBlockList = getCustomBlockLocations(chunk);
		if (customBlockList.isEmpty())
			return;

		for (Location location : customBlockList) {
			Block block = location.getBlock();

			CustomBlockData data = CustomBlockUtils.getData(location);
			if (data.exists())
				return;

			Material material = block.getType();
			final Block below = block.getRelative(BlockFace.DOWN);
			switch (material) {
				case NOTE_BLOCK -> {
					CustomBlockUtils.createData(location, CustomBlock.NOTE_BLOCK, BlockFace.UP);
					block.setBlockData(CustomBlock.NOTE_BLOCK.get().getBlockData(BlockFace.UP, below), false);

					String logMessage = "Creating CustomBlock NoteBlockData at " + StringUtils.getShortLocationString(location);

					CustomBlocksLang.debug(logMessage);
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

					CustomBlockUtils.createData(location, CustomBlock.TRIPWIRE, facing);
					block.setBlockData(CustomBlock.TRIPWIRE.get().getBlockData(facing, below), false);

					String logMessage = "Creating CustomBlock TripwireData at " + StringUtils.getShortLocationString(location);

					CustomBlocksLang.debug(logMessage);
					IOUtils.fileAppend("customblocks", logMessage);
				}
			}
		}
	}

	public static @NonNull List<Location> getCustomBlockLocations(Chunk chunk) {
		return BlockUtils.getBlocksInChunk(chunk, blockData -> CustomBlockType.getBlockMaterials().contains(blockData.getMaterial()));
	}
}
