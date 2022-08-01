package gg.projecteden.nexus.models.clientside;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.PacketUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static gg.projecteden.nexus.utils.Distance.distance;

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
		show(entity);
	}

	public void onRemove(IClientSideEntity<?, ?, ?> entity) {
		hide(entity);
	}

	public boolean canAlreadySee(IClientSideEntity<?, ?, ?> entity) {
		return visibleEntities.contains(entity.getUuid());
	}

	public boolean shouldShow(IClientSideEntity<?, ?, ?> entity) {
		// TODO Conditions
		if (entity.isHidden() && !editing)
			return false;

		if (!isOnline())
			return false;

		if (!entity.location().getWorld().equals(getOnlinePlayer().getWorld()))
			return false;

		if (distanceTo(entity).gt(radius))
			return false;

		return true;
	}

	public void show(List<IClientSideEntity<?, ?, ?>> entities) {
		entities.forEach(this::show);
	}

	public void show(IClientSideEntity<?, ?, ?> entity) {
		if (shouldShow(entity))
			forceShow(entity);
	}

	public void forceShow(List<IClientSideEntity<?, ?, ?>> entities) {
		entities.forEach(this::forceShow);
	}

	public void forceShow(IClientSideEntity<?, ?, ?> entity) {
		if (entity.entity() == null)
			entity.build();

		if (!isOnline())
			return;

		if (!isSameWorld(entity))
			return;

		if (canAlreadySee(entity))
			update(entity);
		else
			spawn(entity);
	}

	public void showAll() {
		if (!isOnline())
			return;

		for (var entity : ClientSideConfig.getEntities(getOnlinePlayer().getLocation().getWorld()))
			show(entity);
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

	public void hide(UUID uuid) {
		hide(ClientSideConfig.getEntity(uuid));
	}

	public void hide(IClientSideEntity<?, ?, ?> entity) {
		sendDestroyPacket(entity);
		visibleEntities.remove(entity.getUuid());
	}

	public int hideAll() {
		int count = visibleEntities.size();

		if (isOnline())
			visibleEntities.forEach(this::sendDestroyPacket);

		visibleEntities.clear();

		return count;
	}

	private void sendDestroyPacket(UUID uuid) {
		sendDestroyPacket(ClientSideConfig.getEntity(uuid));
	}

	private void sendDestroyPacket(IClientSideEntity<?, ?, ?> entity) {
		if (isOnline() && entity != null)
			PacketUtils.entityDestroy(getOnlinePlayer(), entity.id());
	}

	public void updateVisibility(List<IClientSideEntity<?, ?, ?>> entities) {
		entities.forEach(this::updateVisibility);
	}

	public void updateVisibility(IClientSideEntity<?, ?, ?> entity) {
		if (editing)
			forceShow(entity);
		else if (!shouldShow(entity))
			hide(entity);
		else
			forceShow(entity);
	}

}
