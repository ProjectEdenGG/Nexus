package gg.projecteden.nexus.models.perkowner;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(PerkOwner.class)
public class PerkOwnerService extends MongoPlayerService<PerkOwner> {
	private final static Map<UUID, PerkOwner> cache = new ConcurrentHashMap<>();

	public Map<UUID, PerkOwner> getCache() {
		return cache;
	}
}
