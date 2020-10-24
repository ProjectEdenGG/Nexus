package me.pugabyte.bncore.models.skullhunt;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Entity("skull_hunt")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class SkullHunter extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, List<Location>> found = new HashMap<>();

	public void found(String type, Location location) {
		found.putIfAbsent(type, new ArrayList<>()).add(location);
	}

	public List<Location> getFound(String type) {
		return found.get(type);
	}

}
