package me.pugabyte.bncore.models.jigsawjam;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(JigsawJammer.class)
public class JigsawJamService extends MongoService {
	private final static Map<UUID, JigsawJammer> cache = new HashMap<>();

	public Map<UUID, JigsawJammer> getCache() {
		return cache;
	}

}
