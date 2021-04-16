package me.pugabyte.nexus.models.perkowner;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(PerkOwner.class)
public class PerkOwnerService extends MongoService<PerkOwner> {
	private final static Map<UUID, PerkOwner> cache = new HashMap<>();

	public Map<UUID, PerkOwner> getCache() {
		return cache;
	}
}
