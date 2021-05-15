package me.pugabyte.nexus.models.delivery;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static eden.utils.Utils.isNullOrEmpty;

@PlayerClass(DeliveryUser.class)
public class DeliveryService extends MongoService<DeliveryUser> {
	private final static Map<UUID, DeliveryUser> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, DeliveryUser> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	protected boolean deleteIf(DeliveryUser user) {
		return isNullOrEmpty(user.getDeliveries());
	}

}
