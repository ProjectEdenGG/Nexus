package me.pugabyte.nexus.models.punishments;

import dev.morphia.query.Query;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Punishments.class)
public class PunishmentsService extends MongoService<Punishments> {
	private final static Map<UUID, Punishments> cache = new HashMap<>();

	public Map<UUID, Punishments> getCache() {
		return cache;
	}

	public List<Punishments> getAlts(Punishments player) {
		if (player.getIpHistory().isEmpty())
			return new ArrayList<>();

		Query<Punishments> query = database.createQuery(Punishments.class);
		query.and(
				query.criteria("_id").notEqual(player.getUuid()),
				query.criteria("ipHistory.ip").hasAnyOf(player.getIps())
		);

		return query.find().toList();
	}

}
