package gg.projecteden.nexus.models.bearfair21;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;
import gg.projecteden.nexus.utils.Tasks;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(MiniGolf21User.class)
public class MiniGolf21UserService extends MongoService<MiniGolf21User> {
	private final static Map<UUID, MiniGolf21User> cache = new ConcurrentHashMap<>();

	public Map<UUID, MiniGolf21User> getCache() {
		return cache;
	}

	public Collection<MiniGolf21User> getUsers() {
		return getCache().values();
	}

	static {
		Tasks.async(() ->
				database.createQuery(MiniGolf21User.class).find().forEachRemaining(user -> cache.put(user.getUuid(), user)));
	}
}
