package gg.projecteden.nexus.models.wallsofgrace;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.UUID;

@Data
@Entity(value = "walls_of_grace", noClassnameStored = true)
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

	public boolean hasAvailableSign() {
		return sign1 == null && sign2 == null;
	}

}
