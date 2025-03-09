package gg.projecteden.nexus.models.punishments;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(SkinBanConfig.class)
public class SkinBanConfigService extends MongoBukkitService<SkinBanConfig> {
	private final static Map<UUID, SkinBanConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, SkinBanConfig> getCache() {
		return cache;
	}

}
