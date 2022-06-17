package gg.projecteden.nexus.models.customblock;

import gg.projecteden.mongodb.MongoService;
import gg.projecteden.mongodb.annotations.ObjectClass;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(CustomBlockTracker.class)
public class CustomBlockTrackerService extends MongoService<CustomBlockTracker> {
	private final static Map<UUID, CustomBlockTracker> cache = new ConcurrentHashMap<>();

	public Map<UUID, CustomBlockTracker> getCache() {
		return cache;
	}

	public CustomBlockTracker fromWorld(World world) {
		return this.get(world.getUID());
	}

	public CustomBlockTracker fromWorld(Location location) {
		return this.fromWorld(location.getWorld());
	}

	@Override
	protected void beforeSave(CustomBlockTracker object) {
		object.getCustomBlockMap().values().removeIf(Map::isEmpty);
	}

}
