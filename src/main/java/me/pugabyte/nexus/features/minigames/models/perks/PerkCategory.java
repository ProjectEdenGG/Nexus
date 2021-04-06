package me.pugabyte.nexus.features.minigames.models.perks;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

@AllArgsConstructor
@Getter
public enum PerkCategory implements IHasPerkCategory {
	HAT(2),
	TEAM_HAT(2),
	PARTICLE(1),
	ARROW_TRAIL(3),
	GADGET(0)
	;

	/**
	 * Specifies a group of perks of which only one can be enabled, or 0 if the perk can be enabled regardless of the
	 * status of others.<br>
	 * See {@link #excludes(IHasPerkCategory)} to determine if a perk blocks another perk.
	 */
	private final int exclusionGroup;

	@Override
	public String toString() {
		return camelCase(name());
	}

	public boolean isExclusive() {
		return exclusionGroup != 0;
	}

	@Override
	public PerkCategory getPerkCategory() {
		return this;
	}
}
