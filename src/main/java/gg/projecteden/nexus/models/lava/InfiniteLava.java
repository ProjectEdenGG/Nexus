package gg.projecteden.nexus.models.lava;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "infinite_lava", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class InfiniteLava implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled = true;

	public static final List<WorldGroup> DISABLED_WORLDS = List.of(WorldGroup.MINIGAMES, WorldGroup.SKYBLOCK);

	public boolean isEnabled() {
		if (!isOnline())
			return false;

		if (DISABLED_WORLDS.contains(getWorldGroup()))
			return false;

		return enabled;
	}

}
