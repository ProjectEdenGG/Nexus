package gg.projecteden.nexus.models.halloween21;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Halloween21PumpkinHeadConfig.class)
public class Halloween21PumpkinHeadConfigService extends MongoService<Halloween21PumpkinHeadConfig> {
	private final static Map<UUID, Halloween21PumpkinHeadConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, Halloween21PumpkinHeadConfig> getCache() {
		return cache;
	}

}
