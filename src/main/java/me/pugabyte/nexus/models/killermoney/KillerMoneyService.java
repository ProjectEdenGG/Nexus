package me.pugabyte.nexus.models.killermoney;

import eden.mongodb.annotations.PlayerClass;
import lombok.Getter;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(KillerMoney.class)
public class KillerMoneyService extends MongoService<KillerMoney> {

	@Getter
	private final static Map<UUID, KillerMoney> cache = new HashMap<>();

	@Override
	public Map<UUID, KillerMoney> getCache() {
		return cache;
	}
}
