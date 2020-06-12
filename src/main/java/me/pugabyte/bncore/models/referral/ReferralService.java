package me.pugabyte.bncore.models.referral;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Referral.class)
public class ReferralService extends MongoService {
	private final static Map<UUID, Referral> cache = new HashMap<>();

	public Map<UUID, Referral> getCache() {
		return cache;
	}

}
