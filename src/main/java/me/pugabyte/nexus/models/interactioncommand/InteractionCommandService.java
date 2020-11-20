package me.pugabyte.nexus.models.interactioncommand;

import me.pugabyte.nexus.models.MySQLService;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static me.pugabyte.nexus.utils.Utils.sortByKey;

public class InteractionCommandService extends MySQLService {
	private static boolean initialized = false;
	private final static Map<Location, Map<Integer, InteractionCommand>> cache = new HashMap<>();

	public void initialize() {
		cache.clear();
		database.orderBy("location, `index`").results(InteractionCommand.class).forEach(this::cache);
		initialized = true;
	}

	private void cache(InteractionCommand command) {
		if (!cache.containsKey(command.getLocation()))
			cache.put(command.getLocation(), new LinkedHashMap<>());
		cache.get(command.getLocation()).put(command.getIndex(), command);
		cache.put(command.getLocation(), sortByKey(cache.get(command.getLocation())));
	}

	public Map<Integer, InteractionCommand> get(Location location) {
		if (!initialized) initialize();

		if (cache.containsKey(location))
			return new LinkedHashMap<>(cache.get(location));
		else
			return new LinkedHashMap<>();
	}

	public void save(InteractionCommand command) {
		cache(command);
		super.save(command);
	}

	public void delete(InteractionCommand command) {
		cache.get(command.getLocation()).remove(command.getIndex());
		super.delete(command);
	}

	public boolean delete(Location location) {
		if (cache.containsKey(location)) {
			get(location).forEach((index, command) -> delete(command));
			cache.remove(location);
			return true;
		}
		return false;
	}

}
