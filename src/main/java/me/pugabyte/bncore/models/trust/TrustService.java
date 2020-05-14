package me.pugabyte.bncore.models.trust;

import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrustService extends MongoService {
	private final static Map<UUID, Trust> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	@Override
	public Trust get(UUID uuid) {
		cache.computeIfAbsent(uuid, $ -> {
			Trust trust = database.createQuery(Trust.class).field(_id).equal(uuid).first();
			if (trust == null)
				trust = new Trust(uuid);
			return trust;
		});

		return cache.get(uuid);
	}

}
