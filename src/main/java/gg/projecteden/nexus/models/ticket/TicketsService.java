package gg.projecteden.nexus.models.ticket;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Tickets.class)
public class TicketsService extends MongoPlayerService<Tickets> {
	private final static Map<UUID, Tickets> cache = new ConcurrentHashMap<>();

	public Map<UUID, Tickets> getCache() {
		return cache;
	}

}
