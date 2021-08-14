package gg.projecteden.nexus.models.powertool;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.utils.Utils.isNullOrEmpty;

@PlayerClass(PowertoolUser.class)
public class PowertoolService extends MongoService<PowertoolUser> {
	private final static Map<UUID, PowertoolUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, PowertoolUser> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(PowertoolUser user) {
		return isNullOrEmpty(user.getPowertools());
	}

}
