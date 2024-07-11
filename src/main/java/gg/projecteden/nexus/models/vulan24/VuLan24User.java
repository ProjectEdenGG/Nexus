package gg.projecteden.nexus.models.vulan24;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.BoatType;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "vulan24", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class VuLan24User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<Location> found = new HashSet<>();

	private UUID boatUUID;
	private BoatType boatType = BoatType.OAK;

	private static final String PREFIX = StringUtils.getPrefix("VuLan24");

	public static VuLan24User of(HasUniqueId player) {
		return new VuLan24UserService().get(player);
	}

}
