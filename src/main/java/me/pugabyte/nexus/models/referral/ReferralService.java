package me.pugabyte.nexus.models.referral;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Referral.class)
public class ReferralService extends MongoService<Referral> {
	private final static Map<UUID, Referral> cache = new HashMap<>();

	public Map<UUID, Referral> getCache() {
		return cache;
	}

}
