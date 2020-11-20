package me.pugabyte.nexus.models.invisiblearmour;

import me.pugabyte.nexus.models.MySQLService;

import java.util.HashMap;
import java.util.Map;

public class InvisibleArmourService extends MySQLService {
	private final static Map<String, InvisibleArmour> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	@Override
	public InvisibleArmour get(String uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			InvisibleArmour invisibleArmour = database.where("uuid = ?", uuid).first(InvisibleArmour.class);
			if (invisibleArmour.getUuid() == null)
				invisibleArmour = new InvisibleArmour(uuid);
			return invisibleArmour;
		});

		return cache.get(uuid);
	}

}
