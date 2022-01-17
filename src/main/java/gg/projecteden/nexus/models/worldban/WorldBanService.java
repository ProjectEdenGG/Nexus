package gg.projecteden.nexus.models.worldban;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@ObjectClass(WorldBan.class)
public class WorldBanService extends MongoPlayerService<WorldBan> {
	private final static Map<UUID, WorldBan> cache = new ConcurrentHashMap<>();

	public Map<UUID, WorldBan> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(WorldBan worldBan) {
		return isNullOrEmpty(worldBan.getBans());
	}

}
