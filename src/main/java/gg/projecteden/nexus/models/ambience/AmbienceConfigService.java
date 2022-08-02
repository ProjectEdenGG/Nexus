package gg.projecteden.nexus.models.ambience;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(AmbienceConfig.class)
public class AmbienceConfigService extends MongoPlayerService<AmbienceConfig> {
	private final static Map<UUID, AmbienceConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, AmbienceConfig> getCache() {
		return cache;
	}

}
