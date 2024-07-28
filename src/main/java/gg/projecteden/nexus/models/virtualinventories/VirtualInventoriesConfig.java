package gg.projecteden.nexus.models.virtualinventories;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.virtualinventory.models.inventories.VirtualInventory;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "virtual_inventories_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class VirtualInventoriesConfig implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;

	private Map<UUID, VirtualInventory<?>> virtualInventories = new ConcurrentHashMap<>();
	private Map<String, Map<Integer, Map<Integer, Map<Integer, Map<UUID, VirtualInventory<?>>>>>> personalInventories = new ConcurrentHashMap<>();
	private Map<String, Map<Integer, Map<Integer, Map<Integer, VirtualInventory<?>>>>> sharedInventories = new ConcurrentHashMap<>();

}
