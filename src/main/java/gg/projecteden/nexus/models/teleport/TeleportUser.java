package gg.projecteden.nexus.models.teleport;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Entity(value = "teleport_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class TeleportUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Accessors(fluent = true)
	private boolean canBeTeleportedTo = true;

	public boolean canBeTeleportedTo() {
		if (!getRank().isStaff())
			return true;

		return canBeTeleportedTo;
	}

}
