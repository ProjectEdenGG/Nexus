package gg.projecteden.nexus.models.documentation;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(CommandsConfig.class)
public class CommandsConfigService extends MongoPlayerService<CommandsConfig> {
	private final static Map<UUID, CommandsConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, CommandsConfig> getCache() {
		return cache;
	}

}
