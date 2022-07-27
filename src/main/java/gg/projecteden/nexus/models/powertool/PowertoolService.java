package gg.projecteden.nexus.models.powertool;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@ObjectClass(PowertoolUser.class)
public class PowertoolService extends MongoPlayerService<PowertoolUser> {
	private final static Map<UUID, PowertoolUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, PowertoolUser> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(PowertoolUser user) {
		return isNullOrEmpty(user.getPowertools());
	}

}
