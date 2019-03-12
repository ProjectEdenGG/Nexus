package me.pugabyte.bncore.models.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import me.pugabyte.bncore.BNCore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ProtocolHelper {
	public static void clientBlock(Location location, Material item, List<Player> viewers) {
		PacketContainer fakeBlock = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
		fakeBlock.getDoubles().
				write(0, location.getX()).
				write(1, location.getY()).
				write(2, location.getZ());
		fakeBlock.getBlockData().write(0, WrappedBlockData.createData(item));

		try {
			for (Player player : viewers) {
				BNCore.getProtocolManager().sendServerPacket(player, fakeBlock);
			}
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Cannot send packet " + fakeBlock, e);
		}
	}
}
