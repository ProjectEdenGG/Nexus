package me.pugabyte.bncore.models.particleeffect;

import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EffectService extends MongoService {
	public static final Map<UUID, EffectOwner> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	@Override
	public EffectOwner get(UUID uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			EffectOwner effectOwner = database.createQuery(EffectOwner.class).field(_id).equal(uuid).first();
			if (effectOwner == null)
				effectOwner = new EffectOwner(uuid);
			return effectOwner;
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
