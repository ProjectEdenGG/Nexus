package gg.projecteden.nexus.features.nameplates.protocol.packet;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;

public class MountPacket {
	private final PacketContainer packet;

	public MountPacket(int entityId, int passengerId) {
		this.packet = new PacketContainer(Server.MOUNT);
		this.packet.getIntegers().write(0, entityId);
		this.packet.getIntegerArrays().write(0, new int[]{passengerId});
	}

	public PacketContainer getPacket() {
		return this.packet;
	}
}
