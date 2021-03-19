package me.pugabyte.nexus.features.minigames.models.perks;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

@AllArgsConstructor
@Getter
public enum PerkCategory {
	PARTICLE(true),
	HAT(true);

	/**
	 * Whether only one of this perk can be enabled at once.
	 */
	private final boolean exclusive;

	@Override
	public String toString() {
		return camelCase(name());
	}
}
