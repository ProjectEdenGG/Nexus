package me.pugabyte.bncore.models.mysterychest;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.*;
import me.pugabyte.bncore.features.menus.rewardchests.RewardChestType;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity("mystery_chest")
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class MysteryChestPlayer extends PlayerOwnedObject {

	@Id
	@NonNull
	private UUID uuid;
	private Map<RewardChestType, Integer> amounts = new HashMap<>();

	@Override
	public UUID getUuid() {
		return uuid;
	}
}
