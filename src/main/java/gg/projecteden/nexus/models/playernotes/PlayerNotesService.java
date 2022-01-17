package gg.projecteden.nexus.models.playernotes;

import dev.morphia.query.Query;
import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(PlayerNotes.class)
public class PlayerNotesService extends MongoPlayerService<PlayerNotes> {

	private final static Map<UUID, PlayerNotes> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, PlayerNotes> getCache() {
		return cache;
	}

	public List<PlayerNotes> getByKeyword(String keyword) {
		Query<PlayerNotes> query = database.createQuery(PlayerNotes.class);
		query.and(query.criteria("entries.note").containsIgnoreCase(keyword));
		return query.find().toList();
	}

}
