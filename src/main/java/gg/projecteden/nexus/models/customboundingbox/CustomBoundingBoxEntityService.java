package gg.projecteden.nexus.models.customboundingbox;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoEntityService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(CustomBoundingBoxEntity.class)
public class CustomBoundingBoxEntityService extends MongoEntityService<CustomBoundingBoxEntity> {
	private final static Map<UUID, CustomBoundingBoxEntity> cache = new ConcurrentHashMap<>();

	public Map<UUID, CustomBoundingBoxEntity> getCache() {
		return cache;
	}

	@Nullable
	public CustomBoundingBoxEntity getTargetEntity(Player player) {
		if (cache.isEmpty())
			return null;

		final var entity = player.getTargetEntity(15);
		if (entity == null)
			return null;

		final var customBoundingBoxEntity = cache.get(entity.getUniqueId());

		if (customBoundingBoxEntity == null || !customBoundingBoxEntity.hasCustomBoundingBox())
			return null;

		return customBoundingBoxEntity;
	}

	public CustomBoundingBoxEntity getById(String id) {
		return cache.values().stream()
			.filter(stand -> stand.getId().equals(id))
			.findFirst()
			.orElseThrow(() -> new InvalidInputException("CustomBoundingBoxEntity with id &e" + id + " &cnot found"));
	}

}
