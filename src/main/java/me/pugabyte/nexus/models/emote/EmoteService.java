package me.pugabyte.nexus.models.emote;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(EmoteUser.class)
public class EmoteService extends MongoService<EmoteUser> {
	private final static Map<UUID, EmoteUser> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, EmoteUser> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	protected boolean deleteIf(EmoteUser user) {
		return user.getDisabled().isEmpty();
	}

}
