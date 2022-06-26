package gg.projecteden.nexus.models.worldedit;

import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.api.mongodb.annotations.ObjectClass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ForestGeneratorConfig.class)
public class ForestGeneratorConfigService extends MongoService<ForestGeneratorConfig> {
	private final static Map<UUID, ForestGeneratorConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, ForestGeneratorConfig> getCache() {
		return cache;
	}

}
