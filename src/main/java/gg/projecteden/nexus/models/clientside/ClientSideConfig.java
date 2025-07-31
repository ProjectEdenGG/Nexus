package gg.projecteden.nexus.models.clientside;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity.ClientSideEntityType;
import gg.projecteden.nexus.features.events.ArmorStandStalker;
import gg.projecteden.nexus.features.survival.decorationstore.DecorationStore;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "client_side_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class ClientSideConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, List<IClientSideEntity<?, ?, ?>>> entities = new ConcurrentHashMap<>();

	public static final List<ClientSideVisibilityCondition> VISIBILITY_CONDITIONS = new ArrayList<>();
	public static final List<ClientSideItemFrameModifier> ITEM_FRAME_MODIFIERS = new ArrayList<>();

	public static ClientSideConfig get() {
		return new ClientSideConfigService().get0();
	}

	public static Map<String, List<IClientSideEntity<?, ?, ?>>> getEntities() {
		return get().entities;
	}

	public static List<IClientSideEntity<?, ?, ?>> getAllEntities() {
		return getEntities().values().stream().flatMap(Collection::stream).toList();
	}

	public static void save() {
		new ClientSideConfigService().save(get());
	}

	public static List<IClientSideEntity<?, ?, ?>> getEntities(Location location) {
		return getEntities(location.getWorld()).stream()
			.filter(entity -> LocationUtils.isFuzzyEqual(location, entity.getLocation()))
			.toList();
	}

	public static List<IClientSideEntity<?, ?, ?>> getEntities(Location location, double radius) {
		final BoundingBox box = BoundingBox.of(location, radius, radius, radius);
		return getEntities(location.getWorld()).stream()
			.filter(entity -> box.contains(entity.location().toVector()))
			.toList();
	}

	public static List<IClientSideEntity<?, ?, ?>> getEntities(Location location, ClientSideEntityType type, double radius) {
		return getEntities(location, radius).stream()
			.filter(entity -> entity.getType() == type)
			.toList();
	}

	public static List<IClientSideEntity<?, ?, ?>> getEntities(@NotNull World world) {
		return getEntities().computeIfAbsent(world.getName(), $ -> new ArrayList<>());
	}

	public static IClientSideEntity<?, ?, ?> getEntity(UUID uuid) {
		return getAllEntities().stream().filter(entity -> uuid.equals(entity.uuid())).findFirst().orElse(null);
	}

	public static IClientSideEntity<?, ?, ?> getEntity(World world, int id) {
		return getEntities(world).stream().filter(entity -> id == entity.id()).findFirst().orElse(null);
	}

	public static void createEntity(IClientSideEntity<?, ?, ?> entity) {
		getEntities(entity.location().getWorld()).add(entity);
		new ClientSideUserService().getOnline().forEach(user -> user.onCreate(entity));
	}

	public static void delete(World world, int entityId) {
		delete(getEntity(world, entityId));
	}

	public static void delete(IClientSideEntity<?, ?, ?> entity) {
		new ClientSideUserService().getOnline().forEach(user -> user.onRemove(entity));
		getEntities(entity.location().getWorld()).remove(entity);
	}

	public static void onUpdateVisibility(IClientSideEntity<?, ?, ?> entity) {
		new ClientSideUserService().getOnline().forEach(user -> user.updateVisibility(entity));
	}

	private static final NamespacedKey IGNORE_NBT_KEY = new NamespacedKey(Nexus.getInstance(), "clientside.entities.ignore");

	public static void ignoreEntity(org.bukkit.entity.Entity entity) {
		entity.getPersistentDataContainer().set(IGNORE_NBT_KEY, PersistentDataType.BYTE, (byte) 1);
	}

	public static void unignoreEntity(org.bukkit.entity.Entity entity) {
		entity.getPersistentDataContainer().remove(IGNORE_NBT_KEY);
	}

	public static boolean isIgnoredEntity(org.bukkit.entity.Entity entity) {
		if (ArmorStandStalker.isStalker(entity))
			return true;

		if (entity instanceof ArmorStand armorStand)
			if (armorStand.isMarker())
				return true;

		WorldGuardUtils WGUtils = new WorldGuardUtils(entity);
		if (SubWorldGroup.SURVIVAL.contains(WGUtils.getWorld()))
			if (WGUtils.isInRegion(entity.getLocation(), DecorationStore.getStoreRegion()))
				return true;

		final Byte nbt = entity.getPersistentDataContainer().get(IGNORE_NBT_KEY, PersistentDataType.BYTE);
		return nbt != null && nbt == 1;
	}

	public static void registerVisibilityCondition(ClientSideVisibilityCondition condition) {
		VISIBILITY_CONDITIONS.add(condition);
	}

	public static void registerItemFrameModifier(ClientSideItemFrameModifier modifier) {
		ITEM_FRAME_MODIFIERS.add(modifier);
	}

	public abstract static class ClientSideVisibilityCondition {
		public abstract boolean shouldHide(ClientSideUser user, IClientSideEntity<?, ?, ?> entity);
	}

	public abstract static class ClientSideItemFrameModifier {
		public abstract ItemStack modify(ClientSideUser user, ClientSideItemFrame itemFrame);
	}

}
