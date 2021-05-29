package me.pugabyte.nexus.features.minigames.models.perks;

import org.jetbrains.annotations.NotNull;

public interface IHasPerkCategory {

	@NotNull PerkCategory getPerkCategory();

	/**
	 * Determines if this perk prevents another perk from being enabled.
	 * @return true if this perk blocks the other, else false
	 */
	default boolean excludes(IHasPerkCategory other) {
		return getPerkCategory().isExclusive() && getPerkCategory().getExclusionGroup() == other.getPerkCategory().getExclusionGroup();
	}

}
