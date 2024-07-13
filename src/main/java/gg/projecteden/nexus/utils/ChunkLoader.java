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

		loadChunks(getChunks(worldedit.getBlocks(worldguard.getProtectedRegion(region))));
	}

	public static void loadChunks(Set<Chunk> chunks) {
		for (Chunk chunk : chunks) {
			loadChunk(chunk);
		}
	}

	public static void loadChunk(Chunk chunk) {
		chunk.setForceLoaded(true);

		Set<Long> chunks = loadedChunks.get(chunk.getWorld());
		chunks.add(chunk.getChunkKey());

		loadedChunks.put(chunk.getWorld(), chunks);
	}

	private static Set<Chunk> getChunks(List<Block> blocks) {
		Set<Chunk> chunks = new HashSet<>();
		for (Block block : blocks) {
			chunks.add(block.getChunk());
		}
		return chunks;
	}

}
