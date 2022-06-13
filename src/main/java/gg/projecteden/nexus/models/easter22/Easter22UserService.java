package gg.projecteden.nexus.models.easter22;

import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import org.bukkit.Location;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

@ObjectClass(Easter22User.class)
public class Easter22UserService extends MongoPlayerService<Easter22User> {
	private final static Map<UUID, Easter22User> cache = new ConcurrentHashMap<>();

	public Map<UUID, Easter22User> getCache() {
		return cache;
	}

	public List<Easter22User> getTop() {
		return getAll().stream()
			.sorted(Comparator.<Easter22User>comparingInt(user -> user.getFound().size()).reversed())
			.collect(toList());
	}

	public 	Map<Location, Integer> getTopLocations() {
		return new HashMap<>() {{
			for (Easter22User user : new Easter22UserService().getAll())
				for (Location location : user.getFound())
					put(location, getOrDefault(location, 0) + 1);
		}};
	}

}
