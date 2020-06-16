package me.pugabyte.bncore.models.mysterychest;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(MysteryChest.class)
public class MysteryChestService extends MongoService {

	public Map<UUID, MysteryChest> cache = new HashMap<>();

	@Override
	public Map<UUID, MysteryChest> getCache() {
		return cache;
	}
}
