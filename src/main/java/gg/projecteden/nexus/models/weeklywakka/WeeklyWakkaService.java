package gg.projecteden.nexus.models.weeklywakka;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(WeeklyWakka.class)
public class WeeklyWakkaService extends MongoPlayerService<WeeklyWakka> {
	private static final Map<UUID, WeeklyWakka> cache = new ConcurrentHashMap<>();

	public Map<UUID, WeeklyWakka> getCache() {
		return cache;
	}

	public WeeklyWakka get() {
		return super.get0();
	}

}
