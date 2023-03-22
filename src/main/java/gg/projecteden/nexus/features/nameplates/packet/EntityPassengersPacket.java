package gg.projecteden.nexus.features.nameplates.packet;

import gg.projecteden.nexus.features.nameplates.packet.common.NameplatePacket;
import io.netty.buffer.Unpooled;
import lombok.Data;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;

@Data
public class EntityPassengersPacket extends NameplatePacket {
	private final int entityId;
	private final int passengerId;

	@Override
	protected Packet<ClientGamePacketListener> build() {
		return new ClientboundSetPassengersPacket(new FriendlyByteBuf(Unpooled.buffer())
			.writeVarInt(entityId)
			.writeVarIntArray(new int[]{passengerId}));
	}

}
