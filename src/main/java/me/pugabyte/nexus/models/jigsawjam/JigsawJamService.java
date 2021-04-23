package me.pugabyte.nexus.models.jigsawjam;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(JigsawJammer.class)
public class JigsawJamService extends MongoService<JigsawJammer> {
	private final static Map<UUID, JigsawJammer> cache = new HashMap<>();

	public Map<UUID, JigsawJammer> getCache() {
		return cache;
	}

}
