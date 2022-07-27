package gg.projecteden.nexus.models.extraplots;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ExtraPlotUser.class)
public class ExtraPlotUserService extends MongoPlayerService<ExtraPlotUser> {
	private final static Map<UUID, ExtraPlotUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, ExtraPlotUser> getCache() {
		return cache;
	}

}
