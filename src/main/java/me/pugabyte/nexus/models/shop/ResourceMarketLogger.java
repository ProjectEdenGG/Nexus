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
	private List<Coordinate> coordinates = new ArrayList<>();

	private transient Map<Integer, Map<Integer, Map<Integer, Coordinate>>> coordinateMap = new LinkedHashMap<>();

	@PostLoad
	void postLoad() {
		for (Coordinate coordinate : coordinates)
			put(coordinate);
	}

	public void add(Location location) {
		add(new Coordinate(location));
	}

	private void add(Coordinate coordinate) {
		coordinates.add(coordinate);
		put(coordinate);
	}

	private void put(Coordinate coordinate) {
		coordinateMap
			.computeIfAbsent(coordinate.getX(), $ -> new LinkedHashMap<>())
			.computeIfAbsent(coordinate.getZ(), $ -> new LinkedHashMap<>())
			.put(coordinate.getY(), coordinate);
	}

	public boolean contains(Location location) {
		final int x = location.getBlockX();
		final int z = location.getBlockZ();
		final int y = location.getBlockY();

		if (!coordinateMap.containsKey(x))
			return false;

		if (!coordinateMap.get(x).containsKey(z))
			return false;

		if (!coordinateMap.get(x).get(z).containsKey(y))
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
