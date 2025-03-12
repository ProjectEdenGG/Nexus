package gg.projecteden.nexus.models.customblock;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "custom_note_block_entry", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
@Getter
public class CustomNoteBlockTracker implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<Integer, Map<Integer, Map<Integer, NoteBlockData>>> noteBlockMap = new LinkedHashMap<>();
	private Set<Long> convertedChunkKeys = new HashSet<>();

	private void validate(@NonNull Location location) {
		if (!location.getWorld().getUID().equals(uuid))
			throw new InvalidInputException("Using wrong world");
	}

	public void put(@NonNull Location location, @NonNull NoteBlockData data) {
		validate(location);

		noteBlockMap
			.computeIfAbsent(location.getBlockX(), $ -> new LinkedHashMap<>())
			.computeIfAbsent(location.getBlockZ(), $ -> new LinkedHashMap<>())
			.put(location.getBlockY(), data);
	}

	public @Nullable NoteBlockData get(@NonNull Location location) {
		validate(location);

		return noteBlockMap
			.computeIfAbsent(location.getBlockX(), $ -> new LinkedHashMap<>())
			.computeIfAbsent(location.getBlockZ(), $ -> new LinkedHashMap<>())
			.getOrDefault(location.getBlockY(), null);
	}

	public void remove(@NonNull Location location) {
		noteBlockMap
			.computeIfAbsent(location.getBlockX(), $ -> new LinkedHashMap<>())
			.computeIfAbsent(location.getBlockZ(), $ -> new LinkedHashMap<>())
			.remove(location.getBlockY());
	}

	public Map<Location, NoteBlockData> getLocationMap() {
		Map<Location, NoteBlockData> resultMap = new HashMap<>();
		World world = getWorld();
		Map<Integer, Map<Integer, Map<Integer, NoteBlockData>>> locationMap = this.getNoteBlockMap();
		for (Integer x : locationMap.keySet()) {
			for (Integer z : locationMap.get(x).keySet()) {
				for (Integer y : locationMap.get(x).get(z).keySet()) {
					NoteBlockData data = locationMap.get(x).get(z).getOrDefault(y, null);
					if (data != null) {
						Location location = new Location(world, x, y, z).toBlockLocation();
						resultMap.put(location, data);
					}
				}
			}
		}

		return resultMap;
	}

	public @Nullable World getWorld() {
		return Bukkit.getWorld(getUuid());
	}
}
