package me.pugabyte.nexus.models.particle;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(ParticleOwner.class)
public class ParticleService extends MongoService<ParticleOwner> {
	private final static Map<UUID, ParticleOwner> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, ParticleOwner> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	public void saveSync(ParticleOwner particleOwner) {
		database.delete(particleOwner);
		database.save(particleOwner);
	}

}
