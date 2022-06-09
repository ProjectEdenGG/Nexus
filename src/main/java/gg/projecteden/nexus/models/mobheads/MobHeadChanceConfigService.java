package gg.projecteden.nexus.models.mobheads;


import gg.projecteden.mongodb.MongoService;
import gg.projecteden.mongodb.annotations.ObjectClass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(MobHeadChanceConfig.class)
public class MobHeadChanceConfigService extends MongoService<MobHeadChanceConfig> {
	private final static Map<UUID, MobHeadChanceConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, MobHeadChanceConfig> getCache() {
		return cache;
	}

}
