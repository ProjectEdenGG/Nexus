package gg.projecteden.nexus.models.extraplots;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(ExtraPlotUser.class)
public class ExtraPlotUserService extends MongoService<ExtraPlotUser> {
	private final static Map<UUID, ExtraPlotUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, ExtraPlotUser> getCache() {
		return cache;
	}

}
