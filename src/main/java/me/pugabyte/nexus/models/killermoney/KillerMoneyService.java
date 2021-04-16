package me.pugabyte.nexus.models.killermoney;

import lombok.Getter;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
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
