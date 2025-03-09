package gg.projecteden.nexus.models.bigdoor;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(BigDoorConfig.class)
public class BigDoorConfigService extends MongoBukkitService<BigDoorConfig> {
	private final static Map<UUID, BigDoorConfig> cache = new ConcurrentHashMap<>();

	public Map<UUID, BigDoorConfig> getCache() {
		return cache;
	}

	public @NonNull BigDoorConfig get(String doorName) {
		BigDoorConfig config = get(UUID.nameUUIDFromBytes(doorName.getBytes()));
		config.setDoorName(doorName);
		save(config);
		return config;
	}

	public @Nullable BigDoorConfig fromDoorId(long doorId) {
		return getAll().stream().filter(bigDoorConfig -> bigDoorConfig.getDoorId() == doorId).findFirst().orElse(null);
	}
}
