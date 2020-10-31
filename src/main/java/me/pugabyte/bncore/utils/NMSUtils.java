package me.pugabyte.bncore.utils;

import net.minecraft.server.v1_16_R2.BlockPosition;
import net.minecraft.server.v1_16_R2.PacketPlayOutBlockChange;
import net.minecraft.server.v1_16_R2.TileEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSUtils {

	public static void copyTileEntityClient(Player player, Location loc, Block block) {
		BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		TileEntity entity = ((CraftPlayer) player).getHandle().getWorld().getTileEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()));
		PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(pos, entity.getBlock());
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(entity.getUpdatePacket());
	}

}
