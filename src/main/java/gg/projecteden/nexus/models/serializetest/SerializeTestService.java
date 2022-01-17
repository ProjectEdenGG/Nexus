package gg.projecteden.nexus.models.serializetest;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(SerializeTest.class)
public class SerializeTestService extends MongoPlayerService<SerializeTest> {
	private final static Map<UUID, SerializeTest> cache = new ConcurrentHashMap<>();

	public Map<UUID, SerializeTest> getCache() {
		return cache;
	}

}
