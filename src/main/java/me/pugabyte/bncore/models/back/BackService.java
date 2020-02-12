package me.pugabyte.bncore.models.back;

import me.pugabyte.bncore.models.MongoService;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BackService extends MongoService {
	private final static int MAX_LOCATIONS = 10;
	private final static Map<UUID, Back> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	@Override
	public Back get(UUID uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			Back back = database.createQuery(Back.class).field(_id).equal(uuid).first();
			if (back == null)
				back = new Back(uuid);
			return back;
		});

		return cache.get(uuid);
	}

	public void save(Back back) {
		List<Location> locations = back.getLocations();
		if (locations == null || locations.size() == 0)
			super.delete(back);
		else {
			// Trim
			back.setLocations(locations.subList(0, Math.min(locations.size(), MAX_LOCATIONS)));
			super.save(back);
		}
	}
}
