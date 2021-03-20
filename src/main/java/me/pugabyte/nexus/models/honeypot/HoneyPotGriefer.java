package me.pugabyte.nexus.models.honeypot;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.*;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.UUID;

@Data
@Builder
@Entity("honeypot_griefer")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class HoneyPotGriefer extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private double triggered;
	private boolean warned;

}
