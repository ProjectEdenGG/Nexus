package gg.projecteden.nexus.models.ambience;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(AmbienceUser.class)
public class AmbienceUserService extends MongoPlayerService<AmbienceUser> {
	private final static Map<UUID, AmbienceUser> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, AmbienceUser> getCache() {
		return cache;
	}
}
