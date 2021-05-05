package me.pugabyte.nexus.models.weeklywakka;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(WeeklyWakka.class)
public class WeeklyWakkaService extends MongoService<WeeklyWakka> {
	private static final Map<UUID, WeeklyWakka> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, WeeklyWakka> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public WeeklyWakka get() {
		return super.get(Nexus.getUUID0());
	}

}
