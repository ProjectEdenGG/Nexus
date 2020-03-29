package me.pugabyte.bncore.models.interactioncommand;

import me.pugabyte.bncore.models.MySQLService;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractionCommandService extends MySQLService {
	private final static Map<Location, InteractionCommand> cache = new HashMap<>();

	public void clearCache() {
		cache.clear();
	}

	public InteractionCommand get(Location location) {
		if (cache.isEmpty()) {
			List<InteractionCommand> results = database.results(InteractionCommand.class);
			results.forEach(command -> cache.put(command.getLocation(), command));
		}

		return cache.get(location);
	}

	public void save(InteractionCommand command) {
		cache.put(command.getLocation(), command);
		super.save(command);
	}

	public void delete(InteractionCommand command) {
		cache.remove(command.getLocation());
		super.delete(command);
	}

	public void delete(Location location) {
		if (cache.containsKey(location))
			delete(cache.get(location));
	}

}
