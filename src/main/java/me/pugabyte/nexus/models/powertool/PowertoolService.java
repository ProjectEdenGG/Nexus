package me.pugabyte.nexus.models.powertool;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static eden.utils.Utils.isNullOrEmpty;

@PlayerClass(PowertoolUser.class)
public class PowertoolService extends MongoService<PowertoolUser> {
	private final static Map<UUID, PowertoolUser> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, PowertoolUser> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	protected boolean deleteIf(PowertoolUser user) {
		return isNullOrEmpty(user.getPowertools());
	}

}
