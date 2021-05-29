package me.pugabyte.nexus.models.trophy;

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
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.PlayerUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity("trophy")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class TrophyHolder implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<Trophy> earned = new HashSet<>();
	private Set<Trophy> claimed = new HashSet<>();

	public boolean hasEarned(Trophy trophy) {
		return earned.contains(trophy);
	}

	public boolean earn(Trophy trophy) {
		if (hasEarned(trophy))
			return false;

		earned.add(trophy);
		return true;
	}

	public boolean hasClaimed(Trophy trophy) {
		return claimed.contains(trophy);
	}

	public boolean claim(Trophy trophy) {
		if (!hasEarned(trophy))
			throw new InvalidInputException("You have not earned that trophy");

		PlayerUtils.giveItem(getOnlinePlayer(), trophy.getItem().build());
		claimed.add(trophy);
		return true;
	}

}
