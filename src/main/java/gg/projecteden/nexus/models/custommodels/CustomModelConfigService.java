package gg.projecteden.nexus.models.custommodels;

import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.api.mongodb.annotations.ObjectClass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(CustomModelConfig.class)
public class CustomModelConfigService extends MongoService<CustomModelConfig> {
	private final static Map<UUID, CustomModelConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, CustomModelConfig> getCache() {
		return cache;
	}

}
