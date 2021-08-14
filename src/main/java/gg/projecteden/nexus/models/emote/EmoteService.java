package gg.projecteden.nexus.models.emote;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(EmoteUser.class)
public class EmoteService extends MongoService<EmoteUser> {
	private final static Map<UUID, EmoteUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, EmoteUser> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(EmoteUser user) {
		return user.getDisabled().isEmpty();
	}

}
