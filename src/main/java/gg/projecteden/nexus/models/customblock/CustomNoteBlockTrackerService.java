package gg.projecteden.nexus.models.customblock;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(CustomNoteBlockTracker.class)
public class CustomNoteBlockTrackerService extends MongoBukkitService<CustomNoteBlockTracker> {
	private final static Map<UUID, CustomNoteBlockTracker> cache = new ConcurrentHashMap<>();

	public Map<UUID, CustomNoteBlockTracker> getCache() {
		return cache;
	}

	public CustomNoteBlockTracker fromWorld(Location location) {
		return fromWorld(location.getWorld());
	}

	public CustomNoteBlockTracker fromWorld(World world) {
		return this.get(world.getUID());
	}

	@Override
	protected void beforeSave(CustomNoteBlockTracker object) {
		object.getNoteBlockMap().values().removeIf(Map::isEmpty);
		object.getNoteBlockMap().values().forEach(value -> value.values().removeIf(Map::isEmpty));
	}
}
