package gg.projecteden.nexus.models.recipes;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(RecipeUser.class)
public class RecipeUserService extends MongoPlayerService<RecipeUser> {
	private final static Map<UUID, RecipeUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, RecipeUser> getCache() {
		return cache;
	}

}
