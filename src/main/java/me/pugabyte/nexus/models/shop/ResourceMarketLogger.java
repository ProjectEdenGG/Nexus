package me.pugabyte.nexus.models.shop;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Entity("resource_market_logger")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class ResourceMarketLogger implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, List<Coordinate>> coordinates = new HashMap<>();

	private transient Map<String, Map<Integer, Map<Integer, Map<Integer, Coordinate>>>> coordinateMap = new LinkedHashMap<>();

	@PostLoad
	void postLoad() {
		coordinates.forEach((world, coordinates) ->
			coordinates.forEach(coordinate ->
				put(world, coordinate)));
	}

	public void add(Location location) {
		add(location.getWorld().getName(), new Coordinate(location));
	}

	private void add(String world, Coordinate coordinate) {
		coordinates.computeIfAbsent(world, $ -> new ArrayList<>()).add(coordinate);
		put(world, coordinate);
	}

	private void put(String world, Coordinate coordinate) {
		coordinateMap
			.computeIfAbsent(world, $ -> new LinkedHashMap<>())
			.computeIfAbsent(coordinate.getX(), $ -> new LinkedHashMap<>())
			.computeIfAbsent(coordinate.getZ(), $ -> new LinkedHashMap<>())
			.put(coordinate.getY(), coordinate);
	}

	public boolean contains(Location location) {
		final String world = location.getWorld().getName();
		final int x = location.getBlockX();
		final int z = location.getBlockZ();
		final int y = location.getBlockY();

		if (!coordinateMap.containsKey(world))
			return false;

		if (!coordinateMap.get(world).containsKey(x))
			return false;

		if (!coordinateMap.get(world).get(x).containsKey(z))
			return false;

		if (!coordinateMap.get(world).get(x).get(z).containsKey(y))
			return false;

		return true;
	}

	@Data
	@NoArgsConstructor
	private static class Coordinate {
		private int x, y, z;

		public Coordinate(Location location) {
			this.x = location.getBlockX();
			this.y = location.getBlockY();
			this.z = location.getBlockZ();
		}

	}

}
