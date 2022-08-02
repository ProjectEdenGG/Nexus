package gg.projecteden.nexus.models.jigsawjam;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(JigsawJammer.class)
public class JigsawJamService extends MongoPlayerService<JigsawJammer> {
	private final static Map<UUID, JigsawJammer> cache = new ConcurrentHashMap<>();

	public Map<UUID, JigsawJammer> getCache() {
		return cache;
	}

}
