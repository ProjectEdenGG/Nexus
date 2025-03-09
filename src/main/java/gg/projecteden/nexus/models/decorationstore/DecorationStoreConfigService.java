package gg.projecteden.nexus.models.decorationstore;

import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(DecorationStoreConfig.class)
public class DecorationStoreConfigService extends MongoBukkitService<DecorationStoreConfig> {
	private final static Map<UUID, DecorationStoreConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, DecorationStoreConfig> getCache() {
		return cache;
	}

	public @NotNull DecorationStoreConfig get() {
		return super.get(UUIDUtils.UUID0);
	}
}
