package me.pugabyte.nexus.models.delivery;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static eden.utils.Utils.isNullOrEmpty;

@PlayerClass(DeliveryUser.class)
public class DeliveryService extends MongoService<DeliveryUser> {
	private final static Map<UUID, DeliveryUser> cache = new HashMap<>();

	public Map<UUID, DeliveryUser> getCache() {
		return cache;
	}

	@Override
	protected boolean deleteIf(DeliveryUser user) {
		return isNullOrEmpty(user.getDeliveries());
	}

}
