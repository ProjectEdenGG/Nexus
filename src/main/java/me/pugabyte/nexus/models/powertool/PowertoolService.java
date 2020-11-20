package me.pugabyte.nexus.models.powertool;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(PowertoolUser.class)
public class PowertoolService extends MongoService {
	private final static Map<UUID, PowertoolUser> cache = new HashMap<>();

	public Map<UUID, PowertoolUser> getCache() {
		return cache;
	}

	@Override
	public <T> void save(T object) {
		if (((PowertoolUser) object).getPowertools().size() > 0)
			super.save(object);
		else
			super.delete(object);
	}

}
