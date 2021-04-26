package me.pugabyte.nexus.models.wallsofgrace;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.Location;

import java.util.UUID;

@Data
@Builder
@Entity("walls_of_grace")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class WallsOfGrace implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Getter(AccessLevel.PRIVATE)
	private Location sign1;
	@Getter(AccessLevel.PRIVATE)
	private Location sign2;

	public Location get(int id) {
		if (id == 1)
			return sign1;
		else if (id == 2)
			return sign2;
		else
			throw new InvalidInputException("Sign ID must be 1 or 2");
	}

	public void set(int id, Location location) {
		if (id == 1)
			sign1 = location;
		else if (id == 2)
			sign2 = location;
		else
			throw new InvalidInputException("Sign ID must be 1 or 2");
	}

}
