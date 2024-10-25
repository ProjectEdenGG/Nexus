package gg.projecteden.nexus.utils.nms.packet;

import lombok.Data;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;

@Data
public class EntityDestroyPacket extends EdenPacket {
	private final int entityId;

	@Override
	protected Packet<ClientGamePacketListener> build() {
		return new ClientboundRemoveEntitiesPacket(entityId);
	}

}
