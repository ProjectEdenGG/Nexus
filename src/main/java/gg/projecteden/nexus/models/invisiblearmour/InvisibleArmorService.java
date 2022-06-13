package gg.projecteden.nexus.models.invisiblearmour;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(InvisibleArmor.class)
public class InvisibleArmorService extends MongoPlayerService<InvisibleArmor> {
	private final static Map<UUID, InvisibleArmor> cache = new ConcurrentHashMap<>();

	public Map<UUID, InvisibleArmor> getCache() {
		return cache;
	}

}
