package gg.projecteden.nexus.models.survival;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(TurretConfig.class)
public class TurretConfigService extends MongoBukkitService<TurretConfig> {

	private final static Map<UUID, TurretConfig> cache = new ConcurrentHashMap<>();
	static TurretConfigService instance = new TurretConfigService();

	@Override
	public Map<UUID, TurretConfig> getCache() {
		return cache;
	}

	public static TurretConfig get() {
		return TurretConfigService.instance.get0();
	}

}
