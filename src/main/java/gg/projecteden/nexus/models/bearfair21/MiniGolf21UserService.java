package gg.projecteden.nexus.models.bearfair21;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.nexus.utils.Tasks;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(MiniGolf21User.class)
public class MiniGolf21UserService extends MongoPlayerService<MiniGolf21User> {
	private final static Map<UUID, MiniGolf21User> cache = new ConcurrentHashMap<>();

	public Map<UUID, MiniGolf21User> getCache() {
		return cache;
	}

	public Collection<MiniGolf21User> getUsers() {
		return getCache().values();
	}

	static {
		Tasks.async(() -> {
			try (var cursor = database.createQuery(MiniGolf21User.class).find()) {
				cursor.forEachRemaining(user -> cache.put(user.getUuid(), user));
			}
		});
	}
}
