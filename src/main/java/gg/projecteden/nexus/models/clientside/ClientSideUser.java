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

	public void onRemove(IClientSideEntity<?, ?, ?> entity) {
		destroy(entity);
		visibleEntities.remove(entity.getUuid());
	}

	public boolean canSee(IClientSideEntity<?, ?, ?> entity) {
		return visibleEntities.contains(entity.getUuid());
	}

	public boolean isHidden(IClientSideEntity<?, ?, ?> entity) {
		return entity.isHidden() && !editing;
	}

	public void send(List<IClientSideEntity<?, ?, ?>> entities) {
		entities.forEach(this::send);
	}

	public void send(IClientSideEntity<?, ?, ?> entity) {
		if (isHidden(entity))
			return;

		if (canSee(entity))
			update(entity);
		else
			spawn(entity);
	}

	private void spawn(IClientSideEntity<?,?,?> entity) {
		PacketUtils.sendPacket(getOnlinePlayer(), entity.getSpawnPackets());
		update(entity);
		visibleEntities.add(entity.getUuid());
	}

	private void update(IClientSideEntity<?, ?, ?> entity) {
		PacketUtils.sendPacket(getOnlinePlayer(), entity.getUpdatePackets());
	}

	public void destroy(IClientSideEntity<?, ?, ?> entity) {
		entity.destroy(this);
	}

	public void destroy(UUID uuid) {
		destroy(ClientSideConfig.getEntity(uuid));
	}

	public void destroyAll() {
		if (isOnline())
			visibleEntities.forEach(this::destroy);

		visibleEntities.clear();
	}

}
