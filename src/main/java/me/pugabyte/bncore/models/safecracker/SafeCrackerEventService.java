package me.pugabyte.bncore.models.safecracker;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.annotations.Disabled;
import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;
import me.pugabyte.bncore.models.safecracker.SafeCrackerEvent.SafeCrackerGame;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SafeCrackerEvent.class)
@Disabled
public class SafeCrackerEventService extends MongoService {

	private final static Map<UUID, SafeCrackerEvent> cache = new HashMap<>();

	public Map<UUID, SafeCrackerEvent> getCache() {
		return cache;
	}

	public SafeCrackerEvent get() {
		return super.get(BNCore.getUUID0());
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
