package gg.projecteden.nexus.models.sudoku;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(SudokuUser.class)
public class SudokuUserService extends MongoPlayerService<SudokuUser> {
	private final static Map<UUID, SudokuUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, SudokuUser> getCache() {
		return cache;
	}

	@Override
	protected void beforeSave(SudokuUser user) {
		user.getCandidates().forEach((index, row) ->
			row.keySet().removeIf(key -> row.get(key).isEmpty()));
		user.getCandidates().values().removeIf(Map::isEmpty);
		user.getCandidates().keySet().removeIf(key -> user.getCandidates().get(key).isEmpty());
		// TODO Clean up render settings?
	}
}
