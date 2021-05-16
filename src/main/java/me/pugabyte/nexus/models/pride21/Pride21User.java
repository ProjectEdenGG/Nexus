package me.pugabyte.nexus.models.pride21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.events.y2021.pride21.Decorations;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity("pride21")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Pride21User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<Decorations> decorationsCollected = new HashSet<>();
	private int rewardsClaimed = 0;
	public int decorationsFound() {
		return decorationsCollected.size();
	}
	public int decorationsLeft() {
		return Decorations.values().length - decorationsFound();
	}
	public boolean isComplete() {
		return decorationsLeft() == 0;
	}
	public boolean canClaimReward() {
		return rewardsClaimed < 2;
	}
}
