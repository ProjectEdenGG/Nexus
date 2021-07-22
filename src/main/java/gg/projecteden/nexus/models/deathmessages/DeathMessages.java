package gg.projecteden.nexus.models.deathmessages;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity(value = "death_messages", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class DeathMessages implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Behavior behavior = Behavior.SHOWN;
	private LocalDateTime expiration;

	public enum Behavior {
		HIDDEN,
		LOCAL,
		SHOWN
	}

}
