package gg.projecteden.nexus.models.skullhunt;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(SkullHunter.class)
public class SkullHuntService extends MongoPlayerService<SkullHunter> {
	private final static Map<UUID, SkullHunter> cache = new ConcurrentHashMap<>();

	public Map<UUID, SkullHunter> getCache() {
		return cache;
	}

}
