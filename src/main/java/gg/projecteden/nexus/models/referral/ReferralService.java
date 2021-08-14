package gg.projecteden.nexus.models.referral;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Referral.class)
public class ReferralService extends MongoService<Referral> {
	private final static Map<UUID, Referral> cache = new ConcurrentHashMap<>();

	public Map<UUID, Referral> getCache() {
		return cache;
	}

}
