package me.pugabyte.bncore.models.mysterychest;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

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
