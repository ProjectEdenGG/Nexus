package me.pugabyte.nexus.models.delayedban;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(DelayedBan.class)
public class DelayedBanService extends MongoService<DelayedBan> {
	private final static Map<UUID, DelayedBan> cache = new HashMap<>();

	@Override
	public Map<UUID, DelayedBan> getCache() {
		return cache;
	}

}
