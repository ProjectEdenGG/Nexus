package me.pugabyte.nexus.models.punishments;

import dev.morphia.query.Query;
import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.utils.PlayerUtils.Dev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Punishments.class)
public class PunishmentsService extends MongoService<Punishments> {
	private final static Map<UUID, Punishments> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, Punishments> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public List<Punishments> getAlts(Punishments player) {
		return getAlts(Collections.singletonList(player));
	}

	public List<Punishments> getAlts(List<Punishments> players) {
		Query<Punishments> query = database.createQuery(Punishments.class);

		List<String> ips = new ArrayList<String>() {{
			for (Punishments player : players) {
				query.and(query.criteria("_id").notEqual(player.getUuid()));
				addAll(player.getIps());
			}
		}};

		if (ips.isEmpty())
			return new ArrayList<>();

		query.and(
			query.criteria("_id").notEqual(Dev.KODA.getUuid()),
			query.criteria("_id").notEqual(Dev.SPIKE.getUuid()),
			query.criteria("ipHistory.ip").hasAnyOf(ips)
		);

		return query.find().toList();
	}

}
