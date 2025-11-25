package gg.projecteden.nexus.features.virtualinventories.models.tiles;

import gg.projecteden.nexus.features.virtualinventories.managers.VirtualSharedInventoryManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class VirtualChunk {

	private final List<Tile<?>> tiles = new ArrayList<>();
	private final int x;
	private final int z;
	private final World world;
	private boolean forceLoaded;

	public VirtualChunk(int x, int z, World world) {
		this(x, z, world, false);
	}

	public Chunk getBukkitChunk() {
		return world.getChunkAt(x, z);
	}

	public Tile<?> getTile(@NotNull Block block) {
		for (Tile<?> tile : tiles) {
			if (tile.getX() == block.getX() && tile.getY() == block.getY() && tile.getZ() == block.getZ()) {
				return tile;
			}
		}
		return null;
	}

	public boolean addTile(@NotNull Tile<?> tile) {
		if (!tiles.contains(tile)) {
			tiles.add(tile);
			return true;
		}
		return false;
	}

	public boolean removeTile(@NotNull Tile<?> tile) {
		if (tiles.contains(tile)) {
			tiles.remove(tile);
			return true;
		}

		return false;
	}

	public boolean isLoaded() {
		return VirtualSharedInventoryManager.isChunkLoaded(this);
	}

	public boolean isBukkitChunkLoaded() {
		return world.isChunkLoaded(x, z);
	}

	public void tick() {
		for (Tile<?> tile : new ArrayList<>(tiles)) {
			if (tile instanceof TickableTile<?> tickableTile)
				tickableTile.tick();
		}
	}

}
