package gg.projecteden.nexus.models.noteblock;

import gg.projecteden.mongodb.MongoService;
import gg.projecteden.nexus.features.noteblocks.NoteBlockData;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NoteBlockTrackerService extends MongoService<NoteBlockTracker> {
	private final static Map<UUID, NoteBlockTracker> cache = new ConcurrentHashMap<>();

	public Map<UUID, NoteBlockTracker> getCache() {
		return cache;
	}

	public @Nullable NoteBlockData getNoteBlockData(Location from) {
		Map<Location, NoteBlockData> noteBlockMap = get0().getNoteBlockMap();
		return noteBlockMap.get(from.toBlockLocation());
	}
}
