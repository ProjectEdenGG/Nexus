package me.pugabyte.nexus.models.shop;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import eden.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

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
	private Map<String, Map<Integer, Map<Integer, List<Integer>>>> coordinateMap = new LinkedHashMap<>();

	public void add(Location location) {
		coordinateMap
			.computeIfAbsent(location.getWorld().getName(), $ -> new LinkedHashMap<>())
			.computeIfAbsent(location.getBlockX(), $ -> new LinkedHashMap<>())
			.computeIfAbsent(location.getBlockZ(), $ -> new ArrayList<>())
			.add(location.getBlockY());
	}

	public void remove(Location location) {
		final var yList = getYs(location);
		if (Utils.isNullOrEmpty(yList))
			return;

		yList.remove(location.getBlockY());
	}

	public boolean contains(Location location) {
		final var yList = getYs(location);
		if (Utils.isNullOrEmpty(yList))
			return false;

		return yList.contains(location.getBlockY());
	}

	@Nullable
	private List<Integer> getYs(Location location) {
		final var world = coordinateMap.get(location.getWorld().getName());
		if (Utils.isNullOrEmpty(world))
			return null;

		final var x = world.get(location.getBlockX());
		if (Utils.isNullOrEmpty(x))
			return null;

		final var z = x.get(location.getBlockZ());
		if (Utils.isNullOrEmpty(z))
			return null;

		return z;
	}

}
