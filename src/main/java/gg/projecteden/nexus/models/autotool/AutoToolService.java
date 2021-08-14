package gg.projecteden.nexus.models.autotool;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(AutoTool.class)
public class AutoToolService extends MongoService<AutoTool> {
	private final static Map<UUID, AutoTool> cache = new ConcurrentHashMap<>();

	public Map<UUID, AutoTool> getCache() {
		return cache;
	}

}
