package gg.projecteden.nexus.models.skullhunt;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "skull_hunt", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class SkullHunter implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, List<Location>> found = new ConcurrentHashMap<>();

	public void found(String type, Location location) {
		found.putIfAbsent(type, new ArrayList<>()).add(location);
	}

	public List<Location> getFound(String type) {
		return found.get(type);
	}

}
