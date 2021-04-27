package me.pugabyte.nexus.models.worldban;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static eden.utils.Utils.isNullOrEmpty;

@PlayerClass(WorldBan.class)
public class WorldBanService extends MongoService<WorldBan> {
	private final static Map<UUID, WorldBan> cache = new HashMap<>();

	public Map<UUID, WorldBan> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(WorldBan worldBan) {
		return isNullOrEmpty(worldBan.getBans());
	}

}
