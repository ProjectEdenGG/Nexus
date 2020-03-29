package me.pugabyte.bncore.models.interactioncommand;

import me.pugabyte.bncore.models.MySQLService;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
		cache.put(command.getLocation(), cache.get(command.getLocation()).entrySet().stream()
				.sorted(Entry.comparingByKey())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
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
