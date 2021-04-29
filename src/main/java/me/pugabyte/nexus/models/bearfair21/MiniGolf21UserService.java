package me.pugabyte.nexus.models.bearfair21;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.utils.Tasks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(MiniGolf21User.class)
public class MiniGolf21UserService extends MongoService<MiniGolf21User> {
	private final static Map<UUID, MiniGolf21User> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, MiniGolf21User> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public Collection<MiniGolf21User> getUsers() {
		return getCache().values();
	}

	static {
		Tasks.async(() ->
				database.createQuery(MiniGolf21User.class).find().forEachRemaining(user -> cache.put(user.getUuid(), user)));
	}
}
