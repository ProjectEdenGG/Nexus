package me.pugabyte.bncore.models.emote;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(EmoteUser.class)
public class EmoteService extends MongoService {
	private final static Map<UUID, EmoteUser> cache = new HashMap<>();

	public Map<UUID, EmoteUser> getCache() {
		return cache;
	}

}
