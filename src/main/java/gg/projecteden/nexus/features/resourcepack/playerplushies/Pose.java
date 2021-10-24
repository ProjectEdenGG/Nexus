package gg.projecteden.nexus.features.resourcepack.playerplushies;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Pose {
	SITTING(Tier.TIER_1),
	STANDING(Tier.TIER_2),
	;

	private final Tier tier;

	public int getStartingIndex() {
		return ordinal() * 10000;
	}

}
