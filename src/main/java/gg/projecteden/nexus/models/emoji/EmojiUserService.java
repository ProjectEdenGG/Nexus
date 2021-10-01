package gg.projecteden.nexus.models.emoji;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(EmojiUser.class)
public class EmojiUserService extends MongoService<EmojiUser> {
	private final static Map<UUID, EmojiUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, EmojiUser> getCache() {
		return cache;
	}

}
