package gg.projecteden.nexus.models.wordle;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(WordleUser.class)
public class WordleUserService extends MongoPlayerService<WordleUser> {
	private final static Map<UUID, WordleUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, WordleUser> getCache() {
		return cache;
	}
}
