package gg.projecteden.nexus.models.radio;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(RadioUser.class)
public class RadioUserService extends MongoPlayerService<RadioUser> {
	private final static Map<UUID, RadioUser> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, RadioUser> getCache() {
		return cache;
	}

}
