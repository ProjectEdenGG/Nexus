package me.pugabyte.nexus.models.delayedban;

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
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.UUID;

@Data
@Builder
@Entity("delayed_ban")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class})
public class DelayedBan implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private UUID uuid_staff;
	private String reason;
	private String duration;
}
