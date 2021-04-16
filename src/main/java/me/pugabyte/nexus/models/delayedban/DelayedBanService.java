package me.pugabyte.nexus.models.delayedban;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(DelayedBan.class)
public class DelayedBanService extends MongoService {
	private final static Map<UUID, DelayedBan> cache = new HashMap<>();

	@Override
	public Map<UUID, DelayedBan> getCache() {
		return cache;
	}

}
