package me.pugabyte.nexus.models.deathmessages;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity("death_messages")
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
