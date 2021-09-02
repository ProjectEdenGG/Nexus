package gg.projecteden.nexus.features.nameplates.protocol.packet;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import gg.projecteden.nexus.features.nameplates.protocol.packet.common.NameplatePacket;

public class MountPacket extends NameplatePacket {

	public MountPacket(int entityId, int passengerId) {
		super(new PacketContainer(Server.MOUNT));
		this.packet.getIntegers().write(0, entityId);
		this.packet.getIntegerArrays().write(0, new int[]{passengerId});
	}

}
