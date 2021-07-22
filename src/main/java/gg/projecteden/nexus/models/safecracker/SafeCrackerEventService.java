package gg.projecteden.nexus.models.safecracker;

import gg.projecteden.annotations.Disabled;
import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEvent.SafeCrackerGame;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(SafeCrackerEvent.class)
@Disabled
public class SafeCrackerEventService extends MongoService<SafeCrackerEvent> {
	private final static Map<UUID, SafeCrackerEvent> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, SafeCrackerEvent> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public SafeCrackerGame getActiveEvent() {
		for (SafeCrackerGame game : super.get0().getGames().values()) {
			if (game.isActive())
				return game;
		}
		return null;
	}

	public void setActiveGame(SafeCrackerGame game) {
		super.get0().getGames().values().stream().filter(SafeCrackerGame::isActive).forEach(_game -> {
			_game.setActive(false);
		});
		game.setActive(true);
		save(super.get0());
	}

}
