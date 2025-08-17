package gg.projecteden.nexus.models.proportionator;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ProportionatorConfig.class)
public class ProportionatorConfigService extends MongoBukkitService<ProportionatorConfig> {
	private final static Map<UUID, ProportionatorConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, ProportionatorConfig> getCache() {
		return cache;
	}

}
