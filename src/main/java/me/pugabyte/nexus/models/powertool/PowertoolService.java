package me.pugabyte.nexus.models.powertool;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(PowertoolUser.class)
public class PowertoolService extends MongoService<PowertoolUser> {
	private final static Map<UUID, PowertoolUser> cache = new HashMap<>();

	public Map<UUID, PowertoolUser> getCache() {
		return cache;
	}

	@Override
	public void save(PowertoolUser user) {
		if (user.getPowertools().isEmpty())
			super.delete(user);
		else
			super.save(user);
	}

}
