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
		forceLoad(world, region, true);
	}

	public static void forceLoad(World world, String region, boolean forceLoaded) {
		WorldGuardUtils worldguard = new WorldGuardUtils(world);
		ProtectedRegion protectedRegion = worldguard.getProtectedRegion(region);
		forceLoad(world, protectedRegion, forceLoaded);
	}

	public static void forceLoad(World world, ProtectedRegion protectedRegion) {
		forceLoad(world, protectedRegion, true);
	}

	public static void forceLoad(World world, ProtectedRegion protectedRegion, boolean forceLoaded) {
		WorldEditUtils worldedit = new WorldEditUtils(world);
		List<Block> blocks = worldedit.getBlocks(protectedRegion);
		forceLoad(blocks, forceLoaded);
	}

	public static void forceLoad(List<Block> blocks, boolean forceLoaded) {
		forceLoad(blocks.stream().map(Block::getChunk).collect(Collectors.toSet()), forceLoaded);
	}

	public static void forceLoad(Set<Chunk> chunks) {
		forceLoad(chunks, true);
	}

	public static void forceLoad(Set<Chunk> chunks, boolean forceLoaded) {
		for (Chunk chunk : chunks)
			setForceLoaded(chunk, forceLoaded);
	}

	public static void setForceLoaded(Chunk chunk) {
		setForceLoaded(chunk, true);
	}

	public static void setForceLoaded(Chunk chunk, boolean forceLoaded) {
		long chunkKey = chunk.getChunkKey();

		Set<Long> chunks = loadedChunks.getOrDefault(chunk.getWorld(), new HashSet<>());
		if (forceLoaded) {
			chunk.setForceLoaded(true);
			chunk.load();
			chunks.add(chunkKey);
		} else {
			chunk.setForceLoaded(false);
			chunks.remove(chunkKey);
		}

		loadedChunks.put(chunk.getWorld(), chunks);
	}

}
