package gg.projecteden.nexus.models.noteblock;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Entity(value = "note_block_entry", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class NoteBlockTracker implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<Integer, Map<Integer, Map<Integer, NoteBlockData>>> noteBlockMap = new LinkedHashMap<>();

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

	public @NonNull NoteBlockData get(@NonNull Location location) {
		validate(location);

		return noteBlockMap
			.computeIfAbsent(location.getBlockX(), $ -> new LinkedHashMap<>())
			.computeIfAbsent(location.getBlockZ(), $ -> new LinkedHashMap<>())
			.getOrDefault(location.getBlockY(), new NoteBlockData());
	}

	public void remove(@NonNull Location location) {
		put(location, new NoteBlockData());
	}
}
