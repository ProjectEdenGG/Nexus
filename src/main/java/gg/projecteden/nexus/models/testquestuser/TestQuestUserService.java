package gg.projecteden.nexus.models.testquestuser;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(TestQuestUser.class)
public class TestQuestUserService extends MongoService<TestQuestUser> {
	private final static Map<UUID, TestQuestUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, TestQuestUser> getCache() {
		return cache;
	}

}
