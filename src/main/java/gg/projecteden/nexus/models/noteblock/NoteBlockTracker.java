package gg.projecteden.nexus.models.noteblock;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.interfaces.DatabaseObject;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.noteblocks.NoteBlockData;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Entity(value = "note_block_entry", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class NoteBlockTracker implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;

	Map<Location, NoteBlockData> noteBlockMap = new HashMap<>();
}
