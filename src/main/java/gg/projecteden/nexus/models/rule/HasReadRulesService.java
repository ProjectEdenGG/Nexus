package gg.projecteden.nexus.models.rule;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(HasReadRules.class)
public class HasReadRulesService extends MongoService<HasReadRules> {
	private final static Map<UUID, HasReadRules> cache = new ConcurrentHashMap<>();

	public Map<UUID, HasReadRules> getCache() {
		return cache;
	}

}
