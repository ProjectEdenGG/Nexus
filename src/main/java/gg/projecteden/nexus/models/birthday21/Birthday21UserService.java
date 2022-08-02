package gg.projecteden.nexus.models.birthday21;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Birthday21User.class)
public class Birthday21UserService extends MongoPlayerService<Birthday21User> {
	private final static Map<UUID, Birthday21User> cache = new ConcurrentHashMap<>();

	public Map<UUID, Birthday21User> getCache() {
		return cache;
	}
}
