package gg.projecteden.nexus.models.trophy;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity(value = "trophy", noClassnameStored = true)
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

	/**
	 * Earns the trophy and sends a congratulatory message (if the user didn't already earn it)
	 */
	public void earnAndMessage(Trophy trophy) {
		if (earn(trophy))
			PlayerUtils.send(this, JsonBuilder.fromPrefix("Trophy").next("You have earned the ").next(trophy.toString(), NamedTextColor.YELLOW).next("! To view your trophies and claim the item, ")
			.next(new JsonBuilder("click here", NamedTextColor.YELLOW).command("trophy")).next(" or run ").next(new JsonBuilder("/trophy", NamedTextColor.YELLOW).command("trophy")));
	}

	public boolean hasClaimed(Trophy trophy) {
		return claimed.contains(trophy);
	}

	public boolean claim(Trophy trophy) {
		if (!hasEarned(trophy))
			throw new InvalidInputException("You have not earned that trophy");

		if (getWorldGroup() != WorldGroup.SURVIVAL)
			throw new InvalidInputException("You must be in Survival to claim this trophy");

		PlayerUtils.giveItem(this, trophy.getItem().build());
		claimed.add(trophy);
		return true;
	}

}
