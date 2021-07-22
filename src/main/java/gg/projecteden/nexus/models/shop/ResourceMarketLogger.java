package gg.projecteden.nexus.models.shop;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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
	private Map<Integer, Map<Integer, List<Integer>>> coordinateMap = new LinkedHashMap<>();

	private void validate(Location location) {
		if (!location.getWorld().getUID().equals(uuid))
			throw new InvalidInputException("Using wrong world");
	}

	public void add(Location location) {
		validate(location);

		coordinateMap
			.computeIfAbsent(location.getBlockX(), $ -> new LinkedHashMap<>())
			.computeIfAbsent(location.getBlockZ(), $ -> new ArrayList<>())
			.add(location.getBlockY());
	}

	public void remove(Location location) {
		validate(location);

		final var yList = getYs(location);
		if (Utils.isNullOrEmpty(yList))
			return;

		yList.remove(Integer.valueOf(location.getBlockY()));
	}

	public boolean contains(Location location) {
		validate(location);

		final var yList = getYs(location);
		if (Utils.isNullOrEmpty(yList))
			return false;

		return yList.contains(location.getBlockY());
	}

	public int size() {
		AtomicInteger count = new AtomicInteger();
		coordinateMap.forEach((x, zys) -> zys.forEach((z, ys) -> count.addAndGet(ys.size())));
		return count.get();
	}

	@Nullable
	private List<Integer> getYs(Location location) {
		final var x = coordinateMap.get(location.getBlockX());
		if (Utils.isNullOrEmpty(x))
			return null;

		final var z = x.get(location.getBlockZ());
		if (Utils.isNullOrEmpty(z))
			return null;

		return z;
	}

}
