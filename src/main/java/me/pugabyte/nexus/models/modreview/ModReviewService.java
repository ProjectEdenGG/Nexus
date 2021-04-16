package me.pugabyte.nexus.models.modreview;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(ModReview.class)
public class ModReviewService extends MongoService<ModReview> {
	private final static Map<UUID, ModReview> cache = new HashMap<>();

	public Map<UUID, ModReview> getCache() {
		return cache;
	}

}
