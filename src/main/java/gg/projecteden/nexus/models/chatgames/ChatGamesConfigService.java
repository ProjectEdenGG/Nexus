package gg.projecteden.nexus.models.chatgames;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ChatGamesConfig.class)
public class ChatGamesConfigService extends MongoPlayerService<ChatGamesConfig> {
	private final static Map<UUID, ChatGamesConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, ChatGamesConfig> getCache() {
		return cache;
	}

}
