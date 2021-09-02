package gg.projecteden.nexus.features.nameplates.protocol.packet;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import gg.projecteden.nexus.features.nameplates.protocol.packet.common.NameplatePacket;

import java.util.Collections;

public class EntityDestroyPacket extends NameplatePacket {

	public EntityDestroyPacket(int var1) {
		super(new PacketContainer(Server.ENTITY_DESTROY));
		this.packet.getIntLists().write(0, Collections.singletonList(var1));
	}

}
