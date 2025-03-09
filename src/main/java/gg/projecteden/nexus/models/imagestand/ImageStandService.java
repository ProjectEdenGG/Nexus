package gg.projecteden.nexus.models.imagestand;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ImageStand.class)
public class ImageStandService extends MongoBukkitService<ImageStand> {
	private final static Map<UUID, ImageStand> cache = new ConcurrentHashMap<>();
	private final static Map<UUID, UUID> outlineCache = new ConcurrentHashMap<>();

	public Map<UUID, ImageStand> getCache() {
		return cache;
	}

	public Map<UUID, UUID> getOutlineCache() {
		return outlineCache;
	}

	public ImageStand getTargetStand(Player player) {
		if (cache.isEmpty())
			return null;

		final var entity = player.getTargetEntity(42);
		if (!(entity instanceof ArmorStand))
			return null;

		final UUID uuid = entity.getUniqueId();
		final var imageStand = cache.get(uuid);

		if (imageStand != null && imageStand.isActive())
			return imageStand;

		return cache.values().stream()
			.filter(stand -> stand.matches(uuid))
			.findFirst()
			.orElse(null);
	}

	public void removeOutlineFor(Player player) {
		final UUID uuid = outlineCache.get(player.getUniqueId());
		if (uuid == null)
			return;

		get(uuid).removeOutlineFor(player);
	}

	public ImageStand getById(String id) {
		return cache.values().stream()
			.filter(stand -> stand.getId().equalsIgnoreCase(id))
			.findFirst()
			.orElseThrow(() -> new InvalidInputException("Image stand with id &e" + id + " &cnot found"));
	}

}
