package gg.projecteden.nexus.features.nameplates.packet;

import gg.projecteden.nexus.features.nameplates.packet.common.NameplatePacket;
import lombok.Data;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;

import java.util.List;

@Data
public class EntitySneakPacket extends NameplatePacket {
	private final int entityId;
	private boolean sneaking;

	public EntitySneakPacket setSneaking(boolean sneaking) {
		this.sneaking = sneaking;
		return this;
	}

	@Override
	protected Packet<ClientGamePacketListener> build() {
		final var seeThrough = new DataValue<>(26, EntityDataSerializers.BYTE, (byte) (sneaking ? 0 : 2));
		return new ClientboundSetEntityDataPacket(entityId, List.of(seeThrough));
	}

}
