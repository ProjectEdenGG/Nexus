package me.pugabyte.bncore.models.worldban;

import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldBanService extends MongoService {
	private final static Map<UUID, WorldBan> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	@Override
	public WorldBan get(UUID uuid) {
		if (!cache.containsKey(uuid)) {
			WorldBan worldBan = database.createQuery(WorldBan.class).field("_id").equal(uuid).first();
			if (worldBan == null)
				worldBan = new WorldBan(uuid);
			cache.put(uuid, worldBan);
		}

		return cache.get(uuid);
	}

	public List<WorldBan> getAll() {
		return database.createQuery(WorldBan.class).find().toList();
	}

	public void save(WorldBan worldBan) {
		if (worldBan.getBans() == null || worldBan.getBans().size() == 0)
			super.delete(worldBan);
		else
			super.save(worldBan);
	}

}
