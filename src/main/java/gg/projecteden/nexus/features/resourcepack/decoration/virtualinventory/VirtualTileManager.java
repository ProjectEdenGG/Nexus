package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.FurnaceProperties;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualFurnace;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.tiles.FurnaceTile;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.tiles.Tile;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.tiles.VirtualChunk;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.tiles.VirtualChunkKey;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VirtualTileManager {
	private static final Map<VirtualChunkKey, VirtualChunk> chunkMap = new ConcurrentHashMap<>();
	private static final List<VirtualChunk> loadedChunks = new ArrayList<>();
	private static final List<Tile<?>> tiles = new ArrayList<>();
	private static int taskId;

	public static void onStart() {
		loadChunks();

		taskId = Tasks.repeat(TickTime.TICK.x(20), TickTime.TICK, () -> {
			if (!VirtualInventoryManager.isTicking())
				return;

			for (VirtualChunk chunk : getLoadedChunks()) {
				chunk.tick();
			}
		});
	}

	public static void onStop() {
		Tasks.cancel(taskId);
	}

	private static void loadChunks() {
		for (Tile<?> tile : tiles) {
			int x = tile.getX() >> 4;
			int z = tile.getZ() >> 4;
			VirtualChunkKey key = new VirtualChunkKey(x, z);
			VirtualChunk chunk;
			if (!chunkMap.containsKey(key)) {
				chunkMap.put(key, new VirtualChunk(x, z, tile.getBukkitWorld()));
			}
			chunk = chunkMap.get(key);
			chunk.addTile(tile);
			if (tile.getBukkitWorld().isChunkLoaded(x, z)) {
				loadedChunks.add(chunk);
			}
		}
//		Util.log("Loaded: &b" + loadedChunks.size() + "&7/&b" + chunkMap.values().size() + "&7 virtual chunks");
	}

	public static Collection<VirtualChunk> getChunks() {
		return Collections.unmodifiableCollection(chunkMap.values());
	}

	public static Collection<VirtualChunk> getLoadedChunks() {
		return Collections.unmodifiableCollection(loadedChunks);
	}

	public static boolean loadChunk(@NotNull VirtualChunk chunk) {
		return loadedChunks.add(chunk);
	}

	public static boolean unloadChunk(@NotNull VirtualChunk chunk) {
		if (loadedChunks.contains(chunk) && !chunk.isForceLoaded()) {
			loadedChunks.remove(chunk);
			return true;
		}
		return false;
	}

	public static boolean isChunkLoaded(@NotNull VirtualChunk chunk) {
		return loadedChunks.contains(chunk);
	}

	public static VirtualChunk getChunk(int x, int z) {
		return chunkMap.get(new VirtualChunkKey(x, z));
	}

	public static VirtualChunk getChunk(@NotNull Chunk chunk) {
		for (VirtualChunk mChunk : chunkMap.values()) {
			if (chunk.getX() == mChunk.getX() && chunk.getZ() == mChunk.getZ()) {
				return mChunk;
			}
		}
		return null;
	}

	public static Tile<?> getTile(@NotNull Block block) {
		return getTile(block.getX(), block.getY(), block.getZ(), block.getWorld());
	}

	public static Tile<?> getTile(int x, int y, int z, @NotNull World world) {
		for (Tile<?> tile : tiles) {
			if (tile.getX() == x && tile.getY() == y && tile.getZ() == z && tile.getBukkitWorld() == world) {
				return tile;
			}
		}
		return null;
	}

	public static boolean removeTile(@NotNull Tile<?> tile) {
		VirtualChunkKey key = new VirtualChunkKey(tile.getX() >> 4, tile.getZ() >> 4);
		VirtualChunk virtualChunk = chunkMap.get(key);
		if (virtualChunk != null) {
			Dev.WAKKA.send("removing tile...");
			virtualChunk.removeTile(tile);
			tiles.remove(tile);
			return true;
		}

		Dev.WAKKA.send("unknown chunk at " + tile.getX() + " " + tile.getZ());
		return false;
	}

	public static FurnaceTile createFurnaceTile(@NotNull Block block, @NotNull VirtualFurnace furnace) {
		return createFurnaceTile(block.getX(), block.getY(), block.getZ(), block.getWorld(), furnace);
	}

	public static FurnaceTile createFurnaceTile(@NotNull Block block, @NotNull String title, @NotNull FurnaceProperties properties) {
		VirtualFurnace virtualFurnace = new VirtualFurnace(title, properties);
		return createFurnaceTile(block.getX(), block.getY(), block.getZ(), block.getWorld(), virtualFurnace);
	}

	public static FurnaceTile createFurnaceTile(int x, int y, int z, @NotNull World world, @NotNull VirtualFurnace furnace) {
		FurnaceTile tile = new FurnaceTile(furnace, x, y, z, world);

		tiles.add(tile);
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		VirtualChunkKey key = new VirtualChunkKey(chunkX, chunkZ);

		if (!chunkMap.containsKey(key)) {
			chunkMap.put(key, new VirtualChunk(chunkX, chunkZ, world));
		}

		VirtualChunk virtualChunk = chunkMap.get(key);
		if (virtualChunk.isBukkitChunkLoaded()) {
			loadedChunks.add(virtualChunk);
		}

		virtualChunk.addTile(tile);
		return tile;
	}


}
