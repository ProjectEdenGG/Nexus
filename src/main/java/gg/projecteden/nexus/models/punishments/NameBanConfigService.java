package gg.projecteden.nexus.models.punishments;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(NameBanConfig.class)
public class NameBanConfigService extends MongoPlayerService<NameBanConfig> {
	private final static Map<UUID, NameBanConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, NameBanConfig> getCache() {
		return cache;
	}

}
