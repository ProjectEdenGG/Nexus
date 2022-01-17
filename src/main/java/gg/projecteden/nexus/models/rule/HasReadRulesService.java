package gg.projecteden.nexus.models.rule;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(HasReadRules.class)
public class HasReadRulesService extends MongoPlayerService<HasReadRules> {
	private final static Map<UUID, HasReadRules> cache = new ConcurrentHashMap<>();

	public Map<UUID, HasReadRules> getCache() {
		return cache;
	}

}
