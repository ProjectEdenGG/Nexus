package me.pugabyte.nexus.models.safecracker;

import eden.annotations.Disabled;
import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.safecracker.SafeCrackerEvent.SafeCrackerGame;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SafeCrackerEvent.class)
@Disabled
public class SafeCrackerEventService extends MongoService<SafeCrackerEvent> {
	private final static Map<UUID, SafeCrackerEvent> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, SafeCrackerEvent> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public SafeCrackerEvent get() {
		return super.get(Nexus.getUUID0());
	}

	public SafeCrackerGame getActiveEvent() {
		for (SafeCrackerGame game : get().getGames().values()) {
			if (game.isActive())
				return game;
		}
		return null;
	}

	public void setActiveGame(SafeCrackerGame game) {
		get().getGames().values().stream().filter(SafeCrackerGame::isActive).forEach(_game -> {
			_game.setActive(false);
		});
		game.setActive(true);
		save(get());
	}

}
