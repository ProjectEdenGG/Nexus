package gg.projecteden.nexus.models.punishments;

import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.api.mongodb.annotations.ObjectClass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(SkinBanConfig.class)
public class SkinBanConfigService extends MongoService<SkinBanConfig> {
	private final static Map<UUID, SkinBanConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, SkinBanConfig> getCache() {
		return cache;
	}

}
