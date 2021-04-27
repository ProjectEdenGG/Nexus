package me.pugabyte.nexus.models.banker;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Transactions.class)
public class TransactionsService extends MongoService<Transactions> {
	private final static Map<UUID, Transactions> cache = new HashMap<>();

	public Map<UUID, Transactions> getCache() {
		return cache;
	}

}
