package gg.projecteden.nexus.models.shop;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

@Data
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
	private transient List<UUID> fallingBlocks = new ArrayList<>();

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

	public boolean remove(Location location) {
		return run(location, List::remove);
	}

	public boolean contains(Location location) {
		return run(location, List::contains);
	}

	public int size() {
		AtomicInteger count = new AtomicInteger();
		coordinateMap.values().forEach(zys -> zys.values().forEach(ys -> count.addAndGet(ys.size())));
		return count.get();
	}

	private boolean run(Location location, BiFunction<List<Integer>, Integer, Boolean> function) {
		validate(location);
		final var x = coordinateMap.get(location.getBlockX());
		if (Nullables.isNullOrEmpty(x))
			return false;

		final var z = x.get(location.getBlockZ());
		if (Nullables.isNullOrEmpty(z))
			return false;

		return function.apply(z, location.getBlockY());
	}

	public void track(FallingBlock fallingBlock) {
		fallingBlocks.add(fallingBlock.getUniqueId());
	}

	public void untrack(FallingBlock fallingBlock) {
		fallingBlocks.remove(fallingBlock.getUniqueId());
	}

	public boolean isTracked(FallingBlock fallingBlock) {
		return fallingBlocks.contains(fallingBlock.getUniqueId());
	}
}
