package gg.projecteden.nexus.models.deathmessages;

import dev.morphia.query.Query;
import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(DeathMessages.class)
public class DeathMessagesService extends MongoPlayerService<DeathMessages> {
	private final static Map<UUID, DeathMessages> cache = new ConcurrentHashMap<>();

	public Map<UUID, DeathMessages> getCache() {
		return cache;
	}

	public List<DeathMessages> getExpired() {
		Query<DeathMessages> query = database.createQuery(DeathMessages.class);
		query.and(query.criteria("expiration").lessThan(LocalDateTime.now()));
		return query.find().toList();
	}

}
