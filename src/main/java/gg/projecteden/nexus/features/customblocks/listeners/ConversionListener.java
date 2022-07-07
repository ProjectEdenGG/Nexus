package gg.projecteden.nexus.features.customblocks.listeners;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock.CustomBlockType;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomBlockTracker;
import gg.projecteden.nexus.models.customblock.CustomBlockTrackerService;
import gg.projecteden.nexus.utils.Nullables;
import lombok.NonNull;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Disabled
public class ConversionListener implements Listener {
	private static final CustomBlockTrackerService trackerService = new CustomBlockTrackerService();
	private static CustomBlockTracker tracker;
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
		convertCustomBlocks(event.getChunk().getChunkSnapshot(), event.getWorld());
	}

	@EventHandler
	public void on(ChunkLoadEvent event) {
		long key = event.getChunk().getChunkKey();
		if (convertedChunks.contains(key))
			return;

		convertedChunks.add(key);
		convertCustomBlocks(event.getChunk().getChunkSnapshot(), event.getWorld());
	}

	public static void convertCustomBlocks(ChunkSnapshot chunk, World world) {
		if (!isInWorldBorder(world, chunk))
			return;
		// If world is legacy -> convert to basic note block
		// else
		// if newly generated chunk -> convert to basic note block
		// else:
		// convert to basic note block

		// TODO: WORLD CHECK - ONLY AFFECT SURVIVALS?

		List<Location> customBlockList = getCustomBlockLocations(chunk, world);
		if (customBlockList.isEmpty())
			return;

		for (Location location : customBlockList) {
			Block block = location.getBlock();

			CustomBlockData data = CustomBlockUtils.getData(location);
			if (data.exists())
				return;

			Material material = block.getType();
			switch (material) {
				case NOTE_BLOCK -> CustomBlockUtils.createData(location, CustomBlock.NOTE_BLOCK, BlockFace.UP);
				case TRIPWIRE -> {
					MultipleFacing multipleFacing = (MultipleFacing) block.getBlockData();
					BlockFace facing = BlockFace.NORTH;
					if (multipleFacing.getFaces().contains(BlockFace.EAST))
						facing = BlockFace.EAST;

					CustomBlockUtils.createData(location, CustomBlock.TRIPWIRE, facing);
				}
			}
		}
	}

	public static @NonNull List<Location> getCustomBlockLocations(ChunkSnapshot chunk, World world) {
		List<Location> found = new ArrayList<>();
		for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					Material material = chunk.getBlockType(x, y, z);

					if (Nullables.isNullOrAir(material))
						continue;

					if (!CustomBlockType.getBlockMaterials().contains(material))
						continue;

					Location location = new Location(world, (chunk.getX() << 4) + x, y, (chunk.getZ() << 4) + z);
					if (!world.getWorldBorder().isInside(location))
						continue;

					found.add(location);
				}
			}
		}
		return found;
	}

	private static boolean isInWorldBorder(World world, ChunkSnapshot chunk) {
		int y = 0;
		List<Location> corners = new ArrayList<>();

		corners.add(new Location(world, (chunk.getX() << 4), y, (chunk.getZ() << 4)));
		corners.add(new Location(world, (chunk.getX() << 4), y, (chunk.getZ() << 4) + 15));
		corners.add(new Location(world, (chunk.getX() << 4) + 15, y, (chunk.getZ() << 4)));
		corners.add(new Location(world, (chunk.getX() << 4) + 15, y, (chunk.getZ() << 4) + 15));

		for (Location corner : corners) {
			if (!world.getWorldBorder().isInside(corner))
				return false;
		}
		return true;
	}
}
