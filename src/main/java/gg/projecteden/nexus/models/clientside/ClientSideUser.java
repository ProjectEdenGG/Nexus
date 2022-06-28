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

	public void clearVisibleEntities() {
		if (isOnline())
			visibleEntities.forEach(this::destroy);

		visibleEntities.clear();
	}

	private IClientSideEntity<?, ?> destroy(UUID uuid) {
		return ClientSideConfig.get().getEntity(uuid).destroy(getOnlinePlayer());
	}

	public void onRemove(UUID uuid) {
		destroy(uuid);
		visibleEntities.remove(uuid);
	}

}
