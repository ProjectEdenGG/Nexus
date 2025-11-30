package gg.projecteden.nexus.models.minigolf;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(MiniGolfConfig.class)
public class MiniGolfConfigService extends MongoPlayerService<MiniGolfConfig> {
	private final static Map<UUID, MiniGolfConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, MiniGolfConfig> getCache() {
		return cache;
	}

}
