package me.pugabyte.nexus.models.serializetest;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SerializeTest.class)
public class SerializeTestService extends MongoService<SerializeTest> {
	private final static Map<UUID, SerializeTest> cache = new HashMap<>();

	public Map<UUID, SerializeTest> getCache() {
		return cache;
	}

}
