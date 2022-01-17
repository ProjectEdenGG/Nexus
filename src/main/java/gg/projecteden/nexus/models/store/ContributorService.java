package gg.projecteden.nexus.models.store;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.nexus.models.store.Contributor.Purchase;
import gg.projecteden.utils.Utils;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

@ObjectClass(Contributor.class)
public class ContributorService extends MongoPlayerService<Contributor> {
	private final static Map<UUID, Contributor> cache = new ConcurrentHashMap<>();

	public Map<UUID, Contributor> getCache() {
		return cache;
	}

	public List<Purchase> getRecent() {
		return getRecent(0);
	}

	public List<Purchase> getRecent(int count) {
		return map(getCollection().aggregate(new ArrayList<>(List.of(
			unwind("$purchases"),
			replaceRoot("$purchases"),
			sort(descending("timestamp"))
		)) {{
			if (count > 0)
				add(limit(count));
		}}), Purchase.class);
	}

	public List<Contributor> getTop(int count) {
		return getAll().stream()
			.sorted(Comparator.comparing(Contributor::getSum).reversed())
			.collect(Collectors.toList())
			.subList(0, count);
	}

	public List<Contributor> getMonthlyTop(YearMonth month, int count) {
		final ArrayList<Contributor> top = new ArrayList<>(Utils.sortByValueReverse(new HashMap<Contributor, Double>() {{
			for (Contributor contributor : getAll()) {
				final double sum = contributor.getMonthlySum(month);
				if (sum > 0)
					put(contributor, sum);
			}
		}}).keySet());

		return top.subList(0, Math.min(top.size(), count));
	}

}
