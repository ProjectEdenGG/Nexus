package gg.projecteden.nexus.features.nameplates.packet.common;

import gg.projecteden.nexus.utils.PacketUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import org.bukkit.entity.Player;

public abstract class NameplatePacket {

	protected abstract Packet<ClientGamePacketListener> build();

	public void send(Player viewer) {
		PacketUtils.sendPacket(viewer, build());
	}

}
