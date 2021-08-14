package gg.projecteden.nexus.models.rainbowarmor;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(RainbowArmor.class)
public class RainbowArmorService extends MongoService<RainbowArmor> {
	private final static Map<UUID, RainbowArmor> cache = new ConcurrentHashMap<>();

	public Map<UUID, RainbowArmor> getCache() {
		return cache;
	}

}
