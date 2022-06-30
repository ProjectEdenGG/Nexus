package gg.projecteden.nexus.features.nameplates.packet;

import com.comphenix.protocol.events.PacketContainer;
import gg.projecteden.nexus.features.nameplates.packet.common.NameplatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;

public class EntityDestroyPacket extends NameplatePacket {

	public EntityDestroyPacket(int entityId) {
		super(PacketContainer.fromPacket(new ClientboundRemoveEntitiesPacket(entityId)));
	}

}
