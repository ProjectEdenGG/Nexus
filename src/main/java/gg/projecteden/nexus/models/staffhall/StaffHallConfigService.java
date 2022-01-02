package gg.projecteden.nexus.models.staffhall;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(StaffHallConfig.class)
public class StaffHallConfigService extends MongoPlayerService<StaffHallConfig> {
	private final static Map<UUID, StaffHallConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, StaffHallConfig> getCache() {
		return cache;
	}

}
