package gg.projecteden.nexus.models.emoji;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(EmojiUser.class)
public class EmojiUserService extends MongoPlayerService<EmojiUser> {
	private final static Map<UUID, EmojiUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, EmojiUser> getCache() {
		return cache;
	}

}
