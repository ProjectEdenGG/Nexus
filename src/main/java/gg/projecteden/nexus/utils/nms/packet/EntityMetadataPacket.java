package gg.projecteden.nexus.utils.nms.packet;

import lombok.Data;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;

import java.util.List;

@Data
public class EntityMetadataPacket extends EdenPacket {
	private final int entityId;
	private final List<SynchedEntityData.DataValue<?>> entityData;

	@Override
	protected Packet<ClientGamePacketListener> build() {
		return new ClientboundSetEntityDataPacket(entityId, entityData);
	}
}
