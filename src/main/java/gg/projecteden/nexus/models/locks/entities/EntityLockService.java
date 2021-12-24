package gg.projecteden.nexus.models.locks.entities;

import gg.projecteden.mongodb.MongoService;
import gg.projecteden.mongodb.annotations.ObjectClass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(EntityLock.class)
public class EntityLockService extends MongoService<EntityLock> {
	private final static Map<UUID, EntityLock> cache = new ConcurrentHashMap<>();

	public Map<UUID, EntityLock> getCache() {
		return cache;
	}

}
