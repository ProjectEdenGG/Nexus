package gg.projecteden.nexus.models.customhitbox;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoEntityService;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(CustomBoundingBoxEntity.class)
public class CustomBoundingBoxEntityService extends MongoEntityService<CustomBoundingBoxEntity> {
	private final static Map<UUID, CustomBoundingBoxEntity> cache = new ConcurrentHashMap<>();

	public Map<UUID, CustomBoundingBoxEntity> getCache() {
		return cache;
	}

	public CustomBoundingBoxEntity getTargetEntity(Player player) {
		if (cache.isEmpty())
			return null;

		final var entity = player.getTargetEntity(15);
		if (entity == null)
			return null;

		final var customBoundingBoxEntity = cache.get(entity.getUniqueId());

		if (customBoundingBoxEntity == null || !customBoundingBoxEntity.hasCustomHitbox())
			return null;

		return customBoundingBoxEntity;
	}

}
