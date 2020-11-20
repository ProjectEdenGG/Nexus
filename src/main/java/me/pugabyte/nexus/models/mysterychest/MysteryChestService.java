package me.pugabyte.nexus.models.mysterychest;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(MysteryChestPlayer.class)
public class MysteryChestService extends MongoService {

	public Map<UUID, MysteryChestPlayer> cache = new HashMap<>();

	@Override
	public Map<UUID, MysteryChestPlayer> getCache() {
		return cache;
	}
}
