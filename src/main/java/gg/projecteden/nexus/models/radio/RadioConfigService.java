package gg.projecteden.nexus.models.radio;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(RadioConfig.class)
public class RadioConfigService extends MongoPlayerService<RadioConfig> {
	private final static Map<UUID, RadioConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, RadioConfig> getCache() {
		return cache;
	}

}
