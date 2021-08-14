package gg.projecteden.nexus.models.serializetest;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(SerializeTest.class)
public class SerializeTestService extends MongoService<SerializeTest> {
	private final static Map<UUID, SerializeTest> cache = new ConcurrentHashMap<>();

	public Map<UUID, SerializeTest> getCache() {
		return cache;
	}

}
