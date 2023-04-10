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
	private boolean seeThroughWalls;

	public EntitySneakPacket setSeeThroughWalls(boolean seeThroughWalls) {
		this.seeThroughWalls = seeThroughWalls;
		return this;
	}

	@Override
	protected Packet<ClientGamePacketListener> build() {
		final var seeThroughWalls = new DataValue<>(26, EntityDataSerializers.BYTE, (byte) (this.seeThroughWalls ? 0 : 2));
		return new ClientboundSetEntityDataPacket(entityId, List.of(seeThroughWalls));
	}

}
