package gg.projecteden.nexus.models.chatgames;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(ChatGamesConfig.class)
public class ChatGamesConfigService extends MongoService<ChatGamesConfig> {
	private final static Map<UUID, ChatGamesConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, ChatGamesConfig> getCache() {
		return cache;
	}

}
