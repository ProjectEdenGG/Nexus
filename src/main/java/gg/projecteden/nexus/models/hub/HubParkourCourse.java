package gg.projecteden.nexus.models.hub;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.interfaces.DatabaseObject;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "hub_parkour_course", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class HubParkourCourse implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private String name;
	private List<Location> checkpoints = new ArrayList<>();

	public HubParkourCourse(@NonNull UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

}
