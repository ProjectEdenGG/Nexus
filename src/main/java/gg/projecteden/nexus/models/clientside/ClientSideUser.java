package gg.projecteden.nexus.models.clientside;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.PacketUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "client_side_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class ClientSideUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private int radius = 30;
	private List<UUID> visibleEntities = new ArrayList<>();

	private boolean editing;

	public static ClientSideUser of(HasUniqueId uuid) {
		return new ClientSideUserService().get(uuid);
	}

	public static ClientSideUser of(UUID uuid) {
		return new ClientSideUserService().get(uuid);
	}

	public void onCreate(IClientSideEntity<?, ?, ?> entity) {
		send(entity);
	}

	public void onRemove(IClientSideEntity<?, ?, ?> entity) {
		destroy(entity);
	}

	public boolean canSee(IClientSideEntity<?, ?, ?> entity) {
		return visibleEntities.contains(entity.getUuid());
	}

	public boolean isHidden(IClientSideEntity<?, ?, ?> entity) {
		// TODO Conditions
		return entity.isHidden() && !editing;
	}

	public void send(List<IClientSideEntity<?, ?, ?>> entities) {
		entities.forEach(this::send);
	}

	public void send(IClientSideEntity<?, ?, ?> entity) {
		if (!isHidden(entity))
			forceSend(entity);
	}

	public void forceSend(List<IClientSideEntity<?, ?, ?>> entities) {
		entities.forEach(this::forceSend);
	}

	public void forceSend(IClientSideEntity<?, ?, ?> entity) {
		entity.build();

		if (!isOnline())
			return;

		if (!isSameWorld(entity))
			return;

		if (canSee(entity))
			update(entity);
		else
			spawn(entity);
	}

	public void sendAll() {
		if (!isOnline())
			return;

		for (var entity : ClientSideConfig.getEntities(getOnlinePlayer().getLocation().getWorld()))
			send(entity);
	}

	private boolean isSameWorld(IClientSideEntity<?, ?, ?> entity) {
		return entity.location().getWorld().equals(getOnlinePlayer().getWorld());
	}

	private void spawn(IClientSideEntity<?,?,?> entity) {
		PacketUtils.sendPacket(getOnlinePlayer(), entity.getSpawnPackets());
		update(entity);
		visibleEntities.add(entity.getUuid());
	}

	private void update(IClientSideEntity<?, ?, ?> entity) {
		PacketUtils.sendPacket(getOnlinePlayer(), entity.getUpdatePackets());
	}

	public void destroy(UUID uuid) {
		destroy(ClientSideConfig.getEntity(uuid));
	}

	public void destroy(IClientSideEntity<?, ?, ?> entity) {
		sendDestroyPacket(entity);
		visibleEntities.remove(entity.getUuid());
	}

	public int destroyAll() {
		int count = visibleEntities.size();

		if (isOnline())
			visibleEntities.forEach(this::sendDestroyPacket);

		visibleEntities.clear();

		return count;
	}

	public void sendDestroyPacket(UUID uuid) {
		sendDestroyPacket(ClientSideConfig.getEntity(uuid));
	}

	private void sendDestroyPacket(IClientSideEntity<?, ?, ?> entity) {
		if (isOnline())
			PacketUtils.entityDestroy(getOnlinePlayer(), entity.id());
	}

	public void updateVisibility(List<IClientSideEntity<?, ?, ?>> entities) {
		entities.forEach(this::updateVisibility);
	}

	public void updateVisibility(IClientSideEntity<?, ?, ?> entity) {
		if (editing)
			forceSend(entity);
		else if (isHidden(entity))
			destroy(entity);
		else
			forceSend(entity);
	}

}
