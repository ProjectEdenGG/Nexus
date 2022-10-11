package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.tiles;

import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.VirtualTileManager;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualInventory;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class Tile<V extends VirtualInventory> {
	final V virtualInv;
	final int x;
	final int y;
	final int z;
	final String world;
	private final BlockData blockData;

	Tile(@NotNull V virtualInv, int x, int y, int z, @NotNull World world) {
		this(virtualInv, world.getBlockAt(x, y, z));
	}

	Tile(@NotNull V virtualInv, int x, int y, int z, @NotNull String world) {
		this.virtualInv = virtualInv;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		World bukkitWorld = Bukkit.getWorld(world);
		if (bukkitWorld != null) {
			this.blockData = bukkitWorld.getBlockAt(x, y, z).getBlockData();
		} else {
			this.blockData = null;
		}
	}

	Tile(@NotNull V virtualInv, @NotNull Block block) {
		this.virtualInv = virtualInv;
		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
		this.world = block.getWorld().getName();
		this.blockData = block.getBlockData();
	}

	public World getBukkitWorld() {
		return Bukkit.getWorld(world);
	}

	public Block getBlock() {
		return getBukkitWorld().getBlockAt(x, y, z);
	}

	public Location getLocation() {
		return getBlock().getLocation();
	}

	public boolean blockDataMatches(@NotNull Block block) {
		return this.blockData.equals(block.getBlockData());
	}

	public void openInventory(@NotNull Player player) {
		virtualInv.openInventory(player);
	}

	public void breakTile() {
		VirtualTileManager.removeTile(this);
	}

	public int getTick() {
		return getVirtualInv().getTick();
	}

	public void tick() {
		if (blockDataMatches(getBlock())) {
			virtualInv.tick();
		} else {
			breakTile();
		}
	}

	@Override
	public String toString() {
		return "Tile{" +
			"virtualInv=" + virtualInv +
			", x=" + x +
			", y=" + y +
			", z=" + z +
			", world='" + world + '\'' +
			", blockData=" + blockData +
			'}';
	}
}
