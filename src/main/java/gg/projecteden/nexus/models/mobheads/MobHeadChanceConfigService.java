package gg.projecteden.nexus.models.mobheads;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(MobHeadChanceConfig.class)
public class MobHeadChanceConfigService extends MongoBukkitService<MobHeadChanceConfig> {
	private final static Map<UUID, MobHeadChanceConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, MobHeadChanceConfig> getCache() {
		return cache;
	}

}
