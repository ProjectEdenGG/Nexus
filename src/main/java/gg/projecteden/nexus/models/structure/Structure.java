package gg.projecteden.nexus.models.structure;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.survival.structures.models.Spawner;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "structure", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Structure implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;

	Location minPoint;
	private List<Spawner> spawners = new ArrayList<>();

	public Structure(UUID uuid0, Location minPoint) {
		this(uuid0);
		this.minPoint = minPoint;
	}

	public @Nullable Spawner getSpawner(CreatureSpawner spawner) {
		for (Spawner _spawner : spawners) {
			if (spawner.getLocation().equals(_spawner.getLocation()))
				return _spawner;
		}

		return null;
	}
}
