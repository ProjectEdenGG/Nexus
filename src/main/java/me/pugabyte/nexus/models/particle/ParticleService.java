package me.pugabyte.nexus.models.particle;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(ParticleOwner.class)
public class ParticleService extends MongoService<ParticleOwner> {
	public static final Map<UUID, ParticleOwner> cache = new HashMap<>();

	public Map<UUID, ParticleOwner> getCache() {
		return cache;
	}

	@Override
	public void saveSync(ParticleOwner particleOwner) {
		database.delete(particleOwner);
		database.save(particleOwner);
	}

}
