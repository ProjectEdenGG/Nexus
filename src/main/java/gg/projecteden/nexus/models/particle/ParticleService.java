package gg.projecteden.nexus.models.particle;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(ParticleOwner.class)
public class ParticleService extends MongoService<ParticleOwner> {
	private final static Map<UUID, ParticleOwner> cache = new ConcurrentHashMap<>();

	public Map<UUID, ParticleOwner> getCache() {
		return cache;
	}

	@Override
	public void saveSync(ParticleOwner particleOwner) {
		database.delete(particleOwner);
		database.save(particleOwner);
	}

}
