package gg.projecteden.nexus.models.noteblock;

import gg.projecteden.mongodb.MongoService;
import gg.projecteden.mongodb.annotations.ObjectClass;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(NoteBlockTracker.class)
public class NoteBlockTrackerService extends MongoService<NoteBlockTracker> {
	private final static Map<UUID, NoteBlockTracker> cache = new ConcurrentHashMap<>();

	public Map<UUID, NoteBlockTracker> getCache() {
		return cache;
	}

	public NoteBlockTracker get(Location location) {
		return this.get(location.getWorld().getUID());
	}
}
