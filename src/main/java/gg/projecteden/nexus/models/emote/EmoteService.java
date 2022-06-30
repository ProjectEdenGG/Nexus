package gg.projecteden.nexus.models.emote;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(EmoteUser.class)
public class EmoteService extends MongoPlayerService<EmoteUser> {
	private final static Map<UUID, EmoteUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, EmoteUser> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(EmoteUser user) {
		return user.getDisabled().isEmpty();
	}

}
