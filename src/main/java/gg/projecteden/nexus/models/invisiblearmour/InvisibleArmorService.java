package gg.projecteden.nexus.models.invisiblearmour;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(InvisibleArmor.class)
public class InvisibleArmorService extends MongoService<InvisibleArmor> {
	private final static Map<UUID, InvisibleArmor> cache = new ConcurrentHashMap<>();

	public Map<UUID, InvisibleArmor> getCache() {
		return cache;
	}

}
