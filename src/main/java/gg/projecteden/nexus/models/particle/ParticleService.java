package gg.projecteden.nexus.models.particle;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ParticleOwner.class)
public class ParticleService extends MongoPlayerService<ParticleOwner> {
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
