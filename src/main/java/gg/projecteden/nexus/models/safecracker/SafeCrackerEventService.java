package gg.projecteden.nexus.models.safecracker;

import gg.projecteden.annotations.Disabled;
import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEvent.SafeCrackerGame;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(SafeCrackerEvent.class)
@Disabled
public class SafeCrackerEventService extends MongoPlayerService<SafeCrackerEvent> {
	private final static Map<UUID, SafeCrackerEvent> cache = new ConcurrentHashMap<>();

	public Map<UUID, SafeCrackerEvent> getCache() {
		return cache;
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
