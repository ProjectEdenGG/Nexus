package gg.projecteden.nexus.models.quests;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Quester.class)
public class QuesterService extends MongoPlayerService<Quester> {
	private final static Map<UUID, Quester> cache = new ConcurrentHashMap<>();

	public Map<UUID, Quester> getCache() {
		return cache;
	}

}