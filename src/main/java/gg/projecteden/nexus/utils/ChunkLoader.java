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

	public static void loadChunks(World world, ProtectedRegion region) {
		loadChunks(world, region.getId());
	}

	public static void loadChunks(World world, String region) {
		WorldEditUtils worldedit = new WorldEditUtils(world);
		WorldGuardUtils worldguard = new WorldGuardUtils(world);

		ProtectedRegion protectedRegion = worldguard.getProtectedRegion(region);
		List<Block> blocks = worldedit.getBlocks(protectedRegion);
		loadChunks(blocks);
	}

	public static void loadChunks(List<Block> blocks) {
		loadChunks(blocks.stream().map(Block::getChunk).collect(Collectors.toSet()));
	}

	public static void loadChunks(Set<Chunk> chunks) {
		chunks.forEach(ChunkLoader::loadChunk);
	}

	public static void loadChunk(Chunk chunk) {
		chunk.setForceLoaded(true);

		Set<Long> chunks = loadedChunks.getOrDefault(chunk.getWorld(), new HashSet<>());
		chunks.add(chunk.getChunkKey());

		loadedChunks.put(chunk.getWorld(), chunks);
	}

}
