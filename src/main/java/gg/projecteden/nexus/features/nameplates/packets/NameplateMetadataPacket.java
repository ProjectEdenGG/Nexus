package gg.projecteden.nexus.features.nameplates.packets;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import gg.projecteden.nexus.utils.nms.packet.EdenPacket;
import lombok.Data;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import org.joml.Vector3f;

import java.util.List;

import static gg.projecteden.nexus.features.nameplates.NameplatesCommand.TRANSLATION_VERTICAL_OFFSET;

@Data
public class NameplateMetadataPacket extends EdenPacket {
	private final int entityId;
	private String name;
	private boolean seeThroughWalls;

	public NameplateMetadataPacket setName(String text) {
		this.name = text;
		return this;
	}

	public NameplateMetadataPacket setSeeThroughWalls(boolean seeThroughWalls) {
		this.seeThroughWalls = seeThroughWalls;
		return this;
	}

	@Override
	protected Packet<ClientGamePacketListener> build() {
		final var translation = new DataValue<>(11, EntityDataSerializers.VECTOR3, new Vector3f(0, TRANSLATION_VERTICAL_OFFSET, 0));
		final var billboard = new DataValue<>(15, EntityDataSerializers.BYTE, (byte) 3);
		final var text = new DataValue<>(23, EntityDataSerializers.COMPONENT, (Component) WrappedChatComponent.fromJson(name).getHandle());
		final var seeThroughWalls = new DataValue<>(27, EntityDataSerializers.BYTE, (byte) (this.seeThroughWalls ? 0 : 2));
		return new ClientboundSetEntityDataPacket(entityId, List.of(translation, billboard, text, seeThroughWalls));
	}

}
