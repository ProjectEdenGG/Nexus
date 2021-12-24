package gg.projecteden.nexus.models.chat;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Chatter.class)
public class ChatterService extends MongoPlayerService<Chatter> {
	private final static Map<UUID, Chatter> cache = new ConcurrentHashMap<>();

	public Map<UUID, Chatter> getCache() {
		return cache;
	}

}
