package me.pugabyte.nexus.models.worldban;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(WorldBan.class)
public class WorldBanService extends MongoService<WorldBan> {
	private final static Map<UUID, WorldBan> cache = new HashMap<>();

	public Map<UUID, WorldBan> getCache() {
		return cache;
	}

	public void save(WorldBan worldBan) {
		if (worldBan.getBans() == null || worldBan.getBans().size() == 0)
			super.delete(worldBan);
		else
			super.save(worldBan);
	}

}
