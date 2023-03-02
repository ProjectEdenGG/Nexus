package gg.projecteden.nexus.models.decorationstore;

import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.api.common.utils.UUIDUtils.UUID0;

@ObjectClass(DecorationStoreConfig.class)
public class DecorationStoreConfigService extends MongoService<DecorationStoreConfig> {
	private final static Map<UUID, DecorationStoreConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, DecorationStoreConfig> getCache() {
		return cache;
	}

	public @NotNull DecorationStoreConfig get() {
		return super.get(UUID0);
	}
}
