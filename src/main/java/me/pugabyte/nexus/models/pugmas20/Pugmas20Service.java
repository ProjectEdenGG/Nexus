package me.pugabyte.nexus.models.pugmas20;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.utils.Tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Pugmas20User.class)
public class Pugmas20Service extends MongoService {

	public static Map<UUID, Pugmas20User> cache = new HashMap<>();

	@Override
	public Map<UUID, Pugmas20User> getCache() {
		return cache;
	}

	@Override
	public <T> void save(T object) {
		Tasks.async(() -> {
			super.deleteSync(object);
			super.saveSync(object);
		});
	}

}
