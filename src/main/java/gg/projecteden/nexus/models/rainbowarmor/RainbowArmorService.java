package gg.projecteden.nexus.models.rainbowarmor;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(RainbowArmor.class)
public class RainbowArmorService extends MongoPlayerService<RainbowArmor> {
	private final static Map<UUID, RainbowArmor> cache = new ConcurrentHashMap<>();

	public Map<UUID, RainbowArmor> getCache() {
		return cache;
	}

}
