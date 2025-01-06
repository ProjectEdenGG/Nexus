package gg.projecteden.nexus.models.back;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Back.class)
public class BackService extends MongoPlayerService<Back> {
	private final static Map<UUID, Back> cache = new ConcurrentHashMap<>();

	public Map<UUID, Back> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(Back back) {
		return Nullables.isNullOrEmpty(back.getLocations());
	}

}
