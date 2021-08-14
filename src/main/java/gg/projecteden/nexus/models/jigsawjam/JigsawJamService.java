package gg.projecteden.nexus.models.jigsawjam;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(JigsawJammer.class)
public class JigsawJamService extends MongoService<JigsawJammer> {
	private final static Map<UUID, JigsawJammer> cache = new ConcurrentHashMap<>();

	public Map<UUID, JigsawJammer> getCache() {
		return cache;
	}

}
