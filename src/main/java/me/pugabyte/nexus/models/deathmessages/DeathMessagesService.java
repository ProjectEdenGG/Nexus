package me.pugabyte.nexus.models.deathmessages;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(DeathMessages.class)
public class DeathMessagesService extends MongoService<DeathMessages> {
	private final static Map<UUID, DeathMessages> cache = new HashMap<>();

	public Map<UUID, DeathMessages> getCache() {
		return cache;
	}

}
