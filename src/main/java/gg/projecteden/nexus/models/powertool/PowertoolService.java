package gg.projecteden.nexus.models.powertool;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.utils.Nullables;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(PowertoolUser.class)
public class PowertoolService extends MongoPlayerService<PowertoolUser> {
	private final static Map<UUID, PowertoolUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, PowertoolUser> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(PowertoolUser user) {
		return Nullables.isNullOrEmpty(user.getPowertools());
	}

}
