package gg.projecteden.nexus.features.nameplates.packet;

import gg.projecteden.nexus.features.nameplates.packet.common.NameplatePacket;
import lombok.Data;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;

@Data
public class EntityDestroyPacket extends NameplatePacket {
	private final int entityId;

	@Override
	protected Packet<ClientGamePacketListener> build() {
		return new ClientboundRemoveEntitiesPacket(entityId);
	}

}
