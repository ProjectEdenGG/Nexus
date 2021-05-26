package me.pugabyte.nexus.features.minigames.models.perks;

public interface IHasPerkCategory {

	PerkCategory getPerkCategory();

	/**
	 * Determines if this perk prevents another perk from being enabled.
	 * @return true if this perk blocks the other, else false
	 */
	default boolean excludes(IHasPerkCategory other) {
		return getPerkCategory().isExclusive() && getPerkCategory().getExclusionGroup() == other.getPerkCategory().getExclusionGroup();
	}

}
