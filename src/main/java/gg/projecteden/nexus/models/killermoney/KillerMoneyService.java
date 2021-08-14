package gg.projecteden.nexus.models.killermoney;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(KillerMoney.class)
public class KillerMoneyService extends MongoService<KillerMoney> {
	private final static Map<UUID, KillerMoney> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, KillerMoney> getCache() {
		return cache;
	}
}
