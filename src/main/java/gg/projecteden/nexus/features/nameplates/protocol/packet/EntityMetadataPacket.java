package gg.projecteden.nexus.features.nameplates.protocol.packet;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import gg.projecteden.nexus.features.nameplates.protocol.packet.common.NameplatePacket;

import java.util.Optional;

public class EntityMetadataPacket extends NameplatePacket {
	private final WrappedDataWatcher watcher;
	private final WrappedDataWatcherObject entityNameObject = new WrappedDataWatcherObject(2, Registry.getChatComponentSerializer(true));
	private final WrappedDataWatcherObject displayNameObject = new WrappedDataWatcherObject(3, Registry.get(Boolean.class));
	private final WrappedDataWatcherObject radiusObject = new WrappedDataWatcherObject(8, Registry.get(Float.class));

	public EntityMetadataPacket(int entityId) {
		super(new PacketContainer(Server.ENTITY_METADATA));
		this.packet.getModifier().writeDefaults();
		this.packet.getIntegers().write(0, entityId);
		this.watcher = new WrappedDataWatcher();

		this.setRadius(0.0F);
	}

	private void updateCollection() {
		this.packet.getWatchableCollectionModifier().write(0, this.watcher.getWatchableObjects());
	}

	public void setName(String text) {
		Optional<Object> var2 = Optional.of(WrappedChatComponent.fromText(text).getHandle());
		this.watcher.setObject(this.entityNameObject, var2);
		this.watcher.setObject(this.displayNameObject, true);
		this.updateCollection();
	}

	public void setNameJson(String text) {
		Optional<Object> var2 = Optional.of(WrappedChatComponent.fromJson(text).getHandle());
		this.watcher.setObject(this.entityNameObject, var2);
		this.watcher.setObject(this.displayNameObject, true);
		this.updateCollection();
	}

	private void setRadius(float radius) {
		this.watcher.setObject(this.radiusObject, radius);
		this.updateCollection();
	}

}
