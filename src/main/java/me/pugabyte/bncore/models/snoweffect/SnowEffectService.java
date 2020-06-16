package me.pugabyte.bncore.models.snoweffect;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SnowEffect.class)
public class SnowEffectService extends MongoService {
	private final static Map<UUID, SnowEffect> cache = new HashMap<>();

	public Map<UUID, SnowEffect> getCache() {
		return cache;
	}

}
