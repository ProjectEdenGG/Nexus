package gg.projecteden.nexus.models.noteblock;

import gg.projecteden.mongodb.MongoService;
import gg.projecteden.mongodb.annotations.ObjectClass;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(NoteBlockTracker.class)
public class NoteBlockTrackerService extends MongoService<NoteBlockTracker> {
	private final static Map<UUID, NoteBlockTracker> cache = new ConcurrentHashMap<>();

	public Map<UUID, NoteBlockTracker> getCache() {
		return cache;
	}

	public NoteBlockTracker fromWorld(World world) {
		return this.get(world.getUID());
	}

	public NoteBlockTracker fromWorld(Location location) {
		return this.fromWorld(location.getWorld());
	}
}
