package gg.projecteden.nexus.models.referral;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Referral.class)
public class ReferralService extends MongoPlayerService<Referral> {
	private final static Map<UUID, Referral> cache = new ConcurrentHashMap<>();

	public Map<UUID, Referral> getCache() {
		return cache;
	}

}
