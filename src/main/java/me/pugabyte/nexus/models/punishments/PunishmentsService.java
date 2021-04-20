package me.pugabyte.nexus.models.punishments;

import dev.morphia.query.Criteria;
import dev.morphia.query.Query;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.punishments.Punishments.IPHistoryEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@PlayerClass(Punishments.class)
public class PunishmentsService extends MongoService<Punishments> {
	private final static Map<UUID, Punishments> cache = new HashMap<>();

	public Map<UUID, Punishments> getCache() {
		return cache;
	}

	public List<Punishments> getAlts(Punishments player) {
		Nexus.log("=======================================");
		Nexus.log("Finding alts of " + player.getName());
		if (player.getIpHistory().isEmpty())
			return new ArrayList<>();

		Query<Punishments> query = database.createQuery(Punishments.class);
		List<Criteria> criteriaList = new ArrayList<>();
		for (String ip : player.getIpHistory().stream().map(IPHistoryEntry::getIp).collect(toList())) {
			Nexus.log("  Adding criteria: " + ip);
			criteriaList.add(query.criteria("ipHistory.ip").equal(ip));
		}
		query.or(criteriaList.toArray(new Criteria[0]));
		query.and(query.criteria("_id").notEqual(player.getUuid()));
		Nexus.log("=======================================");

		return query.find().toList();
	}

}
