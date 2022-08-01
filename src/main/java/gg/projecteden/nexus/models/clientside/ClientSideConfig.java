package gg.projecteden.nexus.models.clientside;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
	private List<IClientSideEntity<?, ?, ?>> entities = new ArrayList<>();

	public static ClientSideConfig get() {
		return new ClientSideConfigService().get0();
	}

	public static List<IClientSideEntity<?, ?, ?>> getEntities() {
		return get().entities;
	}

	public static Map<World, List<IClientSideEntity<?, ?, ?>>> getEntitiesByWorld() {
		return new HashMap<>() {{
			for (IClientSideEntity<?, ?, ?> entity : getEntities())
				computeIfAbsent(entity.location().getWorld(), $ -> new ArrayList<>()).add(entity);
		}};
	}

	public static void save() {
		new ClientSideConfigService().save(get());
	}

	public static List<IClientSideEntity<?, ?, ?>> getEntities(@NotNull World world) {
		return getEntities().stream().filter(entity -> world.equals(entity.location().getWorld())).toList();
	}

	public static IClientSideEntity<?, ?, ?> getEntity(UUID uuid) {
		return getEntities().stream().filter(entity -> uuid.equals(entity.uuid())).findFirst().orElse(null);
	}

	public static IClientSideEntity<?, ?, ?> getEntity(int id) {
		return getEntities().stream().filter(entity -> id == entity.id()).findFirst().orElse(null);
	}

	public static void createEntity(IClientSideEntity<?, ?, ?> entity) {
		getEntities().add(entity);
		new ClientSideUserService().getOnline().forEach(user -> user.onCreate(entity));
	}

	public static void delete(int entityId) {
		delete(getEntity(entityId));
	}

	public static void delete(IClientSideEntity<?, ?, ?> entity) {
		new ClientSideUserService().getOnline().forEach(user -> user.onRemove(entity));
		getEntities().remove(entity);
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
		final Byte nbt = entity.getPersistentDataContainer().get(IGNORE_NBT_KEY, PersistentDataType.BYTE);
		return nbt != null && nbt == 1;
	}

}
