package me.pugabyte.nexus.models.pvp;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(PVP.class)
public class PVPService extends MongoService<PVP> {

	public static final Map<UUID, PVP> cache = new HashMap<>();

	@Override
	public Map<UUID, PVP> getCache() {
		return cache;
	}
}
