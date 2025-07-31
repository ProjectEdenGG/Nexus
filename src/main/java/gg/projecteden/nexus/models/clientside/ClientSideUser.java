package gg.projecteden.nexus.models.clientside;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ConcurrentLinkedQueueConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.clientside.ClientSideConfig.ClientSideVisibilityCondition;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "client_side_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, ConcurrentLinkedQueueConverter.class})
public class ClientSideUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private int radius = 30;
	private boolean editing;

	private transient Set<UUID> visibleEntities = ConcurrentHashMap.newKeySet();
	private transient BoundingBox visibilityBox;
	private transient Location lastUpdateLocation;

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
		if (entity == null || entity.getUuid() == null)
			return false;

		return visibleEntities.contains(entity.getUuid());
	}

	public boolean shouldShow(IClientSideEntity<?, ?, ?> entity) {
		if (!isOnline())
			return false;

		for (ClientSideVisibilityCondition condition : ClientSideConfig.VISIBILITY_CONDITIONS)
			if (condition.shouldHide(this, entity))
				return false;

		if (entity.isHidden() && !editing)
			return false;

		if (isOutsideRadius(entity))
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
		if (entity.getUuid() == null) {
			Nexus.warn("Cannot send entity with null uuid: " + entity.getType() + " " + StringUtils.xyzw(entity));
			return;
		}

		final Player player = getOnlinePlayer();
		PacketUtils.sendPacket(player, entity.getSpawnPackets(player));
		update(entity);
		visibleEntities.add(entity.getUuid());
	}

	private void update(IClientSideEntity<?, ?, ?> entity) {
		// TODO update notifications
		final Player player = getOnlinePlayer();
		PacketUtils.sendPacket(player, entity.getUpdatePackets(player));
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
		else if (!shouldShow(entity)) {
			if (canAlreadySee(entity))
				hide(entity);
		} else
			forceShow(entity);
	}

	public boolean isInsideRadius(IClientSideEntity<?, ?, ?> entity) {
		return getVisibilityBox().contains(entity.location().toVector());
	}

	public boolean isOutsideRadius(IClientSideEntity<?, ?, ?> entity) {
		return !isInsideRadius(entity);
	}

	public boolean hasMoved() {
		final Location currentLocation = getOnlinePlayer().getLocation();
		if (AFK.isSameLocation(lastUpdateLocation, currentLocation))
			return false;

		lastUpdateLocation = currentLocation;
		return true;
	}

	public BoundingBox getVisibilityBox() {
		if (visibilityBox == null)
			updateVisibilityBox();

		return visibilityBox;
	}

	public void updateVisibilityBox() {
		this.visibilityBox = BoundingBox.of(getOnlinePlayer().getLocation(), radius, radius, radius);
	}

	public void refresh(UUID uuid) {
		refresh(ClientSideConfig.getEntity(uuid));
	}

	public void refresh(IClientSideEntity<?, ?, ?> entity) {
		show(entity);
	}

}
