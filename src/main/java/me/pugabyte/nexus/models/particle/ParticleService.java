package me.pugabyte.nexus.models.particle;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(ParticleOwner.class)
public class ParticleService extends MongoService<ParticleOwner> {
	private final static Map<UUID, ParticleOwner> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

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
