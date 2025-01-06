package gg.projecteden.nexus.models.worldban;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.utils.Nullables;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(WorldBan.class)
public class WorldBanService extends MongoPlayerService<WorldBan> {
	private final static Map<UUID, WorldBan> cache = new ConcurrentHashMap<>();

	public Map<UUID, WorldBan> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(WorldBan worldBan) {
		return Nullables.isNullOrEmpty(worldBan.getBans());
	}

}
