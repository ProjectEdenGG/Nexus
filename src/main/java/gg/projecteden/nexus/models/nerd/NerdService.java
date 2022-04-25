package gg.projecteden.nexus.models.nerd;

import dev.morphia.query.Query;
import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

@ObjectClass(Nerd.class)
public class NerdService extends MongoPlayerService<Nerd> {
	private final static Map<UUID, Nerd> cache = new ConcurrentHashMap<>();

	public Map<UUID, Nerd> getCache() {
		return cache;
	}

	public List<Nerd> find(String partialName) {
		Query<Nerd> query = database.createQuery(Nerd.class);
		query.and(query.criteria("pastNames").containsIgnoreCase(partialName));
		long count = query.count();
		if (count > 50)
			throw new InvalidInputException("Too many name matches for &e" + partialName + " &c(" + count + ")");

		final List<Nerd> fuzzyMatches = query.find().toList();
		final List<Nerd> exactMatches = new ArrayList<>();

		for (Nerd nerd : fuzzyMatches)
			for (String pastName : nerd.getPastNames())
				if (pastName.equalsIgnoreCase(partialName))
					exactMatches.add(nerd);

		Map<UUID, Integer> hoursMap = new HashMap<>() {{
			HoursService service = new HoursService();
			for (Nerd nerd : exactMatches.isEmpty() ? fuzzyMatches : exactMatches)
				put(nerd.getUuid(), service.get(nerd).getTotal());
		}};

		Set<UUID> sorted = Utils.sortByValueReverse(hoursMap).keySet();
		return new ArrayList<>(sorted).stream()
				.map(Nerd::of)
				.collect(toList());
	}

	@Nullable
	public Nerd findExact(String name) {
		Query<Nerd> query = database.createQuery(Nerd.class);
		query.and(query.criteria("name").equalIgnoreCase(name));
		return query.find().tryNext();
	}

	public List<Nerd> getNerdsWithBirthdays() {
		Query<Nerd> query = database.createQuery(Nerd.class);
		query.and(query.criteria("birthday").notEqual(null));
		return query.find().toList();
	}

	public Nerd getFromAlias(String alias) {
		Query<Nerd> query = database.createQuery(Nerd.class);
		query.and(query.criteria("aliases").hasThisOne(alias));
		return query.find().tryNext();
	}

}
