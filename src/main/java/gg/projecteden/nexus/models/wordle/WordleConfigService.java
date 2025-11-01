package gg.projecteden.nexus.models.wordle;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(WordleConfig.class)
public class WordleConfigService extends MongoBukkitService<WordleConfig> {
	private final static Map<UUID, WordleConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, WordleConfig> getCache() {
		return cache;
	}

}
