package gg.projecteden.nexus.utils.nms.packet;

import gg.projecteden.nexus.utils.nms.PacketUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import org.bukkit.entity.Player;

public abstract class EdenPacket {

	protected abstract Packet<ClientGamePacketListener> build();

	public void send(Player viewer) {
		PacketUtils.sendPacket(viewer, build());
	}
}
