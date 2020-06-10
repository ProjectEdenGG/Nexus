package me.pugabyte.bncore.models.powertool;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(PowertoolUser.class)
public class PowertoolService extends MongoService {
	private final static Map<UUID, PowertoolUser> cache = new HashMap<>();

	public Map<UUID, PowertoolUser> getCache() {
		return cache;
	}

}
