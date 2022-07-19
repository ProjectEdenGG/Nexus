package gg.projecteden.nexus.models.crate;

import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.api.mongodb.annotations.ObjectClass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(CrateConfig.class)
public class CrateConfigService extends MongoService<CrateConfig> {

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
