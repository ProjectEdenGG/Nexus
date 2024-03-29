package gg.projecteden.nexus.models.testquestuser;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(TestQuestUser.class)
public class TestQuestUserService extends MongoPlayerService<TestQuestUser> {
	private final static Map<UUID, TestQuestUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, TestQuestUser> getCache() {
		return cache;
	}

}
