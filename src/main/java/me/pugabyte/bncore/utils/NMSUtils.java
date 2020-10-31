package me.pugabyte.bncore.utils;

import net.minecraft.server.v1_16_R2.BlockPosition;
import net.minecraft.server.v1_16_R2.PacketPlayOutBlockChange;
import net.minecraft.server.v1_16_R2.TileEntitySkull;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSUtils {

	public static void copyTileEntityClient(Player player, Block origin, Location destination) {
		BlockPosition destinationPosition = new BlockPosition(destination.getBlockX(), destination.getBlockY(), destination.getBlockZ());
		BlockPosition originPosition = new BlockPosition(origin.getX(), origin.getY(), origin.getZ());
		TileEntitySkull tile = (TileEntitySkull) ((CraftPlayer) player).getHandle().getWorld().getTileEntity(originPosition);
		PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(destinationPosition, tile.getBlock());
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(tile.getUpdatePacket());
	}

}
