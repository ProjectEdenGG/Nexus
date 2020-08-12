package me.pugabyte.bncore.models.delayedban;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;

import java.util.UUID;

@Data
@Builder
@Entity("delayed_ban")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class})
public class DelayedBan extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private UUID uuid_staff;
	private String reason;
	private String duration;
}
