package gg.projecteden.nexus.models.store;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;
import gg.projecteden.nexus.models.store.Contributor.Purchase;

import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
			Aggregates.unwind("$purchases"),
			Aggregates.replaceRoot("$purchases"),
			Aggregates.sort(Sorts.descending("timestamp"))
		)) {{
			if (count > 0)
				add(Aggregates.limit(count));
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
