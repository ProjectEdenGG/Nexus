package gg.projecteden.nexus.models.wallsofgrace;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(WallsOfGrace.class)
public class WallsOfGraceService extends MongoPlayerService<WallsOfGrace> {
	private final static Map<UUID, WallsOfGrace> cache = new ConcurrentHashMap<>();

	public Map<UUID, WallsOfGrace> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(WallsOfGrace wallsOfGrace) {
		return wallsOfGrace.get(1) == null && wallsOfGrace.get(2) == null;
	}

}
