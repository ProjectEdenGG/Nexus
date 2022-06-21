package gg.projecteden.nexus.models.costume;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(CostumeUser.class)
public class CostumeUserService extends MongoPlayerService<CostumeUser> {
	private final static Map<UUID, CostumeUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, CostumeUser> getCache() {
		return cache;
	}

	/*
	@Override
	protected boolean deleteIf(CostumeUser user) {
		return user.getVouchers() == 0 && user.getActiveCostume() == null && isNullOrEmpty(user.getOwnedCostumes());
	}
	*/

}
