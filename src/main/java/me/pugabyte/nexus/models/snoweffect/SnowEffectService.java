package me.pugabyte.nexus.models.snoweffect;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SnowEffect.class)
public class SnowEffectService extends MongoService<SnowEffect> {
	private final static Map<UUID, SnowEffect> cache = new HashMap<>();

	public Map<UUID, SnowEffect> getCache() {
		return cache;
	}

}
