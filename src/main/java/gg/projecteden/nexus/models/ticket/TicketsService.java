package gg.projecteden.nexus.models.ticket;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Tickets.class)
public class TicketsService extends MongoService<Tickets> {
	private final static Map<UUID, Tickets> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, Tickets> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

}
