package gg.projecteden.nexus.models.fakenpcs.config;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(FakeNPCConfig.class)
public class FakeNPCConfigService extends MongoBukkitService<FakeNPCConfig> {
	private final static Map<UUID, FakeNPCConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, FakeNPCConfig> getCache() {
		return cache;
	}

}
