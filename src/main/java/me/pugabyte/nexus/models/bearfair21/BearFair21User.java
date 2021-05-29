package me.pugabyte.nexus.models.bearfair21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.resources.fishing.FishingLoot.JunkWeight;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity("bearfair21_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class BearFair21User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	// Quest Stuff
	private Set<Location> clientsideLocations = new HashSet<>();
	private JunkWeight junkWeight = JunkWeight.MAX;
	private int recycledItems = 0;
	private Set<Integer> metNPCs = new HashSet<>();

	public void addRecycledItems(int count) {
		this.recycledItems += count;
		// TODO BF21: Decrease user junkWeight depending on their recycled items
	}

	public boolean hasMet(int npcId) {
		return getMetNPCs().contains(npcId);
	}

}
