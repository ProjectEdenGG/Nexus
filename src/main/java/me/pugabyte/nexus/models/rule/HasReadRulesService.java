package me.pugabyte.nexus.models.rule;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(HasReadRules.class)
public class HasReadRulesService extends MongoService<HasReadRules> {
	private final static Map<UUID, HasReadRules> cache = new HashMap<>();

	public Map<UUID, HasReadRules> getCache() {
		return cache;
	}

}
