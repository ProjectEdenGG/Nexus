package gg.projecteden.nexus.models.clientside;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
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

	public static List<IClientSideEntity<?, ?, ?>> getEntities(@NotNull World world) {
		return get().entities.stream().filter(entity -> world.equals(entity.location().getWorld())).toList();
	}

	public static IClientSideEntity<?, ?, ?> getEntity(UUID uuid) {
		return get().entities.stream().filter(entity -> uuid.equals(entity.uuid())).findFirst().orElse(null);
	}

	public static IClientSideEntity<?, ?, ?> getEntity(int id) {
		return get().entities.stream().filter(entity -> id == entity.id()).findFirst().orElse(null);
	}

	public static void deleteEntity(int entityId) {
		final IClientSideEntity<?, ?, ?> entity = getEntity(entityId);
		new ClientSideUserService().getOnline().forEach(user -> user.onRemove(entity));
		get().entities.remove(entity);
	}

}
