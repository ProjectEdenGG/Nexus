package me.pugabyte.nexus.models.killermoney;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(KillerMoney.class)
public class KillerMoneyService extends MongoService<KillerMoney> {
	private final static Map<UUID, KillerMoney> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	@Override
	public Map<UUID, KillerMoney> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}
}
