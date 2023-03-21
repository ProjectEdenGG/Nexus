package gg.projecteden.nexus.features.nameplates.packet;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import gg.projecteden.nexus.features.nameplates.packet.common.NameplatePacket;
import lombok.Data;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;

import java.util.Collections;

@Data
public class EntityMetadataPacket extends NameplatePacket {
	private final int entityId;
	private String name;

	public EntityMetadataPacket setName(String text) {
		this.name = text;
		return this;
	}

	@Override
	protected Packet<ClientGamePacketListener> build() {
		final var data = new DataValue<>(24, EntityDataSerializers.COMPONENT, (Component) WrappedChatComponent.fromJson(name).getHandle());
		return new ClientboundSetEntityDataPacket(entityId, Collections.singletonList(data));
	}

}
