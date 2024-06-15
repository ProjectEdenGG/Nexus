package gg.projecteden.nexus.models.pugmas24;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Entity(value = "advent24_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, ItemStackConverter.class})
public class Advent24Config implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private Location lootOrigin;
	private Map<Integer, Advent24Present> days = new HashMap<>();

	public static Advent24Config get() {
		return new Advent24ConfigService().get0();
	}

	public Advent24Present get(int day) {
		return days.get(day);
	}

	public Collection<Advent24Present> getPresents() {
		return days.values();
	}

	public void set(int day, Location location) {
		days.put(day, new Advent24Present(day, location));
	}

	public Advent24Present get(Location location) {
		for (Advent24Present present : getPresents())
			if (present.getLocation().equals(location.toBlockLocation()))
				return present;
		return null;
	}
}
