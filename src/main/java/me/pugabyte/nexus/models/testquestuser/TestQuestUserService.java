package me.pugabyte.nexus.models.testquestuser;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(TestQuestUser.class)
public class TestQuestUserService extends MongoService<TestQuestUser> {
	private final static Map<UUID, TestQuestUser> cache = new HashMap<>();

	public Map<UUID, TestQuestUser> getCache() {
		return cache;
	}

}
