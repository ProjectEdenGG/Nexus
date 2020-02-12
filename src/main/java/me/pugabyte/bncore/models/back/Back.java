package me.pugabyte.bncore.models.back;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity("back")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Back extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Property(concreteClass = Location.class)
	private List<Location> locations = new ArrayList<>();

	public void add(Location from) {
		locations.add(from);
	}

}
