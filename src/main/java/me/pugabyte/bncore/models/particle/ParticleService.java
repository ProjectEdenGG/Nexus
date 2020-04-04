package me.pugabyte.bncore.models.particle;

import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParticleService extends MongoService {
	public static final Map<UUID, ParticleOwner> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	@Override
	public ParticleOwner get(UUID uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			ParticleOwner particleOwner = database.createQuery(ParticleOwner.class).field(_id).equal(uuid).first();
			if (particleOwner == null)
				particleOwner = new ParticleOwner(uuid);
			return particleOwner;
		});

		return cache.get(uuid);
	}

//	public void save(EffectOwner effectOwner) {
//		if (effectOwner.getSettings() == null || effectOwner.getSettings().size() == 0)
//			super.delete(effectOwner);
//		else
//			super.save(effectOwner);
//	}

}
