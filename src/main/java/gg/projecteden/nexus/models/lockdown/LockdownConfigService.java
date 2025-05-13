package gg.projecteden.nexus.models.lockdown;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(LockdownConfig.class)
public class LockdownConfigService extends MongoService<LockdownConfig> {
	private final static Map<UUID, LockdownConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, LockdownConfig> getCache() {
		return cache;
	}

}
