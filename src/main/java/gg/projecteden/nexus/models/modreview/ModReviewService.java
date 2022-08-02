package gg.projecteden.nexus.models.modreview;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ModReview.class)
public class ModReviewService extends MongoPlayerService<ModReview> {
	private final static Map<UUID, ModReview> cache = new ConcurrentHashMap<>();

	public Map<UUID, ModReview> getCache() {
		return cache;
	}

}
