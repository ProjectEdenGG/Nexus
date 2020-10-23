package me.pugabyte.bncore.models.skullhunt;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SkullHunter.class)
public class SkullHuntService extends MongoService {
	private final static Map<UUID, SkullHunter> cache = new HashMap<>();

	public Map<UUID, SkullHunter> getCache() {
		return cache;
	}

}
