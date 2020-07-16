package me.pugabyte.bncore.models.delivery;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;
import me.pugabyte.bncore.models.back.Back;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Delivery.class)
public class DeliveryService extends MongoService {
	private final static Map<UUID, Back> cache = new HashMap<>();

	public Map<UUID, Back> getCache() {
		return cache;
	}
}
