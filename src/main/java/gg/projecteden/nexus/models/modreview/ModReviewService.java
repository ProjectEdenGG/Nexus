package gg.projecteden.nexus.models.modreview;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(ModReview.class)
public class ModReviewService extends MongoService<ModReview> {
	private final static Map<UUID, ModReview> cache = new ConcurrentHashMap<>();

	public Map<UUID, ModReview> getCache() {
		return cache;
	}

}
