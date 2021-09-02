package gg.projecteden.nexus.features.nameplates.protocol.packet;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;

import java.util.Collections;

public class EntityDestroyPacket {
	private final PacketContainer packet;

	public EntityDestroyPacket(int var1) {
		this.packet = new PacketContainer(Server.ENTITY_DESTROY);
		this.packet.getIntLists().write(0, Collections.singletonList(var1));
	}

	public PacketContainer getPacket() {
		return this.packet;
	}

}
