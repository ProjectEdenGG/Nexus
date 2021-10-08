package gg.projecteden.nexus.models.costume;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(CostumeUser.class)
public class CostumeUserService extends MongoService<CostumeUser> {
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
