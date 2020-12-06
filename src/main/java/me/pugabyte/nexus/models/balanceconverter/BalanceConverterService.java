package me.pugabyte.nexus.models.balanceconverter;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(BalanceConverter.class)
public class BalanceConverterService extends MongoService {
	private final static Map<UUID, BalanceConverter> cache = new HashMap<>();

	public Map<UUID, BalanceConverter> getCache() {
		return cache;
	}

}
