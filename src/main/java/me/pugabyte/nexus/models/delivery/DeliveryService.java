package me.pugabyte.nexus.models.delivery;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(DeliveryUser.class)
public class DeliveryService extends MongoService {
	private final static Map<UUID, DeliveryUser> cache = new HashMap<>();

	public Map<UUID, DeliveryUser> getCache() {
		return cache;
	}
}
