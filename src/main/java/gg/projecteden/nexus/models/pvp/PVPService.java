package gg.projecteden.nexus.models.pvp;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(PVP.class)
public class PVPService extends MongoPlayerService<PVP> {
	private final static Map<UUID, PVP> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, PVP> getCache() {
		return cache;
	}
}
