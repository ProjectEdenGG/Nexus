package gg.projecteden.nexus.models.decorationstore;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "decoration_store", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class DecorationStoreConfig implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;

	private boolean active;
	private int schematicId;
	private List<DecorationStorePasteHistory> layoutHistory = new ArrayList<>();
	private final static int MAX_ENTRIES = 10;

	private int schematicIdTest;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class DecorationStorePasteHistory {
		LocalDateTime dateTime;
		int schematicId;
	}

	public void addHistory(int schematicId) {
		DecorationStorePasteHistory history = new DecorationStorePasteHistory(LocalDateTime.now(), schematicId);

		layoutHistory.addFirst(history);
		layoutHistory = new ArrayList<>(layoutHistory.subList(0, Math.min(layoutHistory.size(), MAX_ENTRIES)));
	}

}
