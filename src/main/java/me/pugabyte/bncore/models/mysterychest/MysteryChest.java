package me.pugabyte.bncore.models.mysterychest;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.*;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity("mystery_chest")
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class MysteryChest extends PlayerOwnedObject {

	@Id
	@NonNull
	private UUID uuid;
	private int amount;

	@Override
	public UUID getUuid() {
		return uuid;
	}
}
