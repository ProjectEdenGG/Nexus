package me.pugabyte.nexus.models.contributor;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.contributor.Contributor.Purchase;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.replaceRoot;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Aggregates.unwind;
import static com.mongodb.client.model.Sorts.descending;

@PlayerClass(Contributor.class)
public class ContributorService extends MongoService<Contributor> {
	private final static Map<UUID, Contributor> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, Contributor> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public List<Purchase> getRecent(int count) {
		return map(getCollection().aggregate(List.of(
			unwind("$purchases"),
			replaceRoot("$purchases"),
			sort(descending("timestamp")),
			limit(count)
		)), Purchase.class);
	}

	public List<Contributor> getTop(int count) {
		return database.createQuery(Contributor.class).find().toList().stream()
				.sorted(Comparator.comparing(Contributor::getSum).reversed())
				.collect(Collectors.toList())
				.subList(0, count);
	}

}
