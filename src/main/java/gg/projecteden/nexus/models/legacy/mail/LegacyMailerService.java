package gg.projecteden.nexus.models.legacy.mail;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(LegacyMailer.class)
public class LegacyMailerService extends MongoPlayerService<LegacyMailer> {
	private final static Map<UUID, LegacyMailer> cache = new ConcurrentHashMap<>();

	public Map<UUID, LegacyMailer> getCache() {
		return cache;
	}

}
