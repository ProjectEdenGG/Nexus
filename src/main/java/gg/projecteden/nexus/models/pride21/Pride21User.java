package gg.projecteden.nexus.models.pride21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.y2021.pride21.Decorations;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "pride21", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Pride21User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean joinedParade;
	private Set<Decorations> decorationsCollected = new HashSet<>();
	private int rewardsClaimed = 0;
	private boolean bonusTokenRewardClaimed = false; // 50 event tokens + 16 dye bombs for completing the quest

	/**
	 * Gets the number of decorations the player has found for the quest.
	 * @return decorations found
	 */
	public int decorationsFound() {
		return decorationsCollected.size();
	}

	/**
	 * Gets the number of decorations the player has left to find to complete the quest.
	 * @return decorations remaining
	 */
	public int decorationsLeft() {
		return Decorations.values().length - decorationsFound();
	}

	/**
	 * Gets whether or not the player has completed the quest.
	 * @return if the player has completed the quest
	 */
	public boolean isComplete() {
		return decorationsLeft() == 0;
	}

	/**
	 * Gets whether or not this player has rewards available to claim.
	 * Returns false if the player has not finished the quest.
	 * @return if the player can claim a reward
	 */
	public boolean canClaimReward() {
		return isComplete() && rewardsClaimed < 2;
	}

	/**
	 * Gets the rewards this player has left from completing the quest.
	 * Returns 0 if the player has not finished the quest.
	 * @return a number between 0 and 2
	 */
	public int rewardsLeft() {
		return isComplete() ? 2 - rewardsClaimed : 0;
	}

	/**
	 * Attempts to claim a reward from completing the quest, returns true if successful.
	 * This will increment the claimed rewards count and automatically save.
	 * @return if a reward has been successfully claimed
	 */
	public boolean claimReward() {
		if (!canClaimReward())
			return false;
		rewardsClaimed += 1;
		new Pride21UserService().save(this);
		return true;
	}
}
