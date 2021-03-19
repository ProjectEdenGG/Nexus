package me.pugabyte.nexus.features.minigames.models.perks;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(PerkOwner.class)
public class PerkOwnerService extends MongoService {
	private final static Map<UUID, PerkOwner> cache = new HashMap<>();

	public Map<UUID, PerkOwner> getCache() {
		return cache;
	}
}
