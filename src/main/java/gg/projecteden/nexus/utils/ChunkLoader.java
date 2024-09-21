package gg.projecteden.nexus.utils;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.Events;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import lombok.NoArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Depends({Events.class, EdenEvent.class})
@NoArgsConstructor
public class ChunkLoader extends Feature {

	private static final Map<World, Set<Long>> loadedChunks = new HashMap<>();

	@Override
	public void onStop() {
		for (World world : loadedChunks.keySet()) {
			for (Long chunkKey : loadedChunks.get(world)) {
				world.getChunkAt(chunkKey).setForceLoaded(false);
			}

		}
	}

	public static void forceLoad(World world, String region) {
		WorldGuardUtils worldguard = new WorldGuardUtils(world);
		ProtectedRegion protectedRegion = worldguard.getProtectedRegion(region);
		forceLoad(world, protectedRegion);
	}

	public static void forceLoad(World world, ProtectedRegion protectedRegion) {
		WorldEditUtils worldedit = new WorldEditUtils(world);
		List<Block> blocks = worldedit.getBlocks(protectedRegion);
		forceLoad(blocks);
	}

	public static void forceLoad(List<Block> blocks) {
		forceLoad(blocks.stream().map(Block::getChunk).collect(Collectors.toSet()));
	}

	public static void forceLoad(Set<Chunk> chunks) {
		chunks.forEach(ChunkLoader::setForceLoaded);
	}

	public static void setForceLoaded(Chunk chunk) {
		long chunkKey = chunk.getChunkKey();

		Set<Long> chunks = loadedChunks.getOrDefault(chunk.getWorld(), new HashSet<>());
		if (chunks.contains(chunkKey) && chunk.isForceLoaded())
			return;

		chunk.setForceLoaded(true);
		chunk.load();
		chunks.add(chunkKey);
		loadedChunks.put(chunk.getWorld(), chunks);
	}

}
