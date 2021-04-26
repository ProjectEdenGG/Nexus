package me.pugabyte.nexus.models.back;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@PlayerClass(Back.class)
public class BackService extends MongoService<Back> {
	private final static Map<UUID, Back> cache = new HashMap<>();

	public Map<UUID, Back> getCache() {
		return cache;
	}

	private final static int MAX_LOCATIONS = 10;

	public void save(Back back) {
		List<Location> locations = back.getLocations();
		if (locations == null || locations.size() == 0)
			super.delete(back);
		else {
			// Trim
			back.setLocations(locations.subList(0, Math.min(locations.size(), MAX_LOCATIONS)).stream()
					.filter(location -> location.getWorld() != null)
					.collect(Collectors.toList()));
			super.save(back);
		}
	}
}
