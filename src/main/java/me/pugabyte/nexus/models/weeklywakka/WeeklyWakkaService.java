package me.pugabyte.nexus.models.weeklywakka;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(WeeklyWakka.class)
public class WeeklyWakkaService extends MongoService<WeeklyWakka> {

	public static Map<UUID, WeeklyWakka> cache = new HashMap<>();

	public Map<UUID, WeeklyWakka> getCache() {
		return cache;
	}

	public WeeklyWakka get() {
		return super.get(Nexus.getUUID0());
	}

}
