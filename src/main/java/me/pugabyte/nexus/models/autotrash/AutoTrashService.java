package me.pugabyte.nexus.models.autotrash;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(AutoTrash.class)
public class AutoTrashService extends MongoService<AutoTrash> {
	private final static Map<UUID, AutoTrash> cache = new HashMap<>();

	public Map<UUID, AutoTrash> getCache() {
		return cache;
	}

}
