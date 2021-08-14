package gg.projecteden.nexus.models.weeklywakka;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(WeeklyWakka.class)
public class WeeklyWakkaService extends MongoService<WeeklyWakka> {
	private static final Map<UUID, WeeklyWakka> cache = new ConcurrentHashMap<>();

	public Map<UUID, WeeklyWakka> getCache() {
		return cache;
	}

	public WeeklyWakka get() {
		return super.get0();
	}

}
