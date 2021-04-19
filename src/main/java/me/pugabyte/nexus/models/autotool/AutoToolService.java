package me.pugabyte.nexus.models.autotool;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(AutoTool.class)
public class AutoToolService extends MongoService<AutoTool> {
	private final static Map<UUID, AutoTool> cache = new HashMap<>();

	public Map<UUID, AutoTool> getCache() {
		return cache;
	}

}
