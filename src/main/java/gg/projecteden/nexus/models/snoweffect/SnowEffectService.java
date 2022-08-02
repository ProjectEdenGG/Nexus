package gg.projecteden.nexus.models.snoweffect;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(SnowEffect.class)
public class SnowEffectService extends MongoPlayerService<SnowEffect> {
	private final static Map<UUID, SnowEffect> cache = new ConcurrentHashMap<>();

	public Map<UUID, SnowEffect> getCache() {
		return cache;
	}

}
