package gg.projecteden.nexus.models.crate;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(CrateConfig.class)
public class CrateConfigService extends MongoBukkitService<CrateConfig> {

	private final static Map<UUID, CrateConfig> cache = new ConcurrentHashMap<>();
	static CrateConfigService instance = new CrateConfigService();

	@Override
	public Map<UUID, CrateConfig> getCache() {
		return cache;
	}

	public static CrateConfig get() {
		return CrateConfigService.instance.get0();
	}

}
