package gg.projecteden.nexus.models.locks.users;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.locks.blocks.BlockLock;
import gg.projecteden.nexus.models.locks.blocks.BlockLockService;
import gg.projecteden.nexus.models.locks.common.LockType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.UUID;

@Data
@Entity(value = "lock_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class LockUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private LockType defaultLockType;

	public void create(Location location) {
		final BlockLockService service = new BlockLockService();

		if (service.get(location) != null)
			throw new InvalidInputException("Lock already exists at that location");

		service.add(new BlockLock(uuid, location.toBlockLocation(), defaultLockType));
	}

}
