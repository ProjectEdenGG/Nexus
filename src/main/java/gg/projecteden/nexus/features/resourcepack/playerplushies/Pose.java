package gg.projecteden.nexus.features.resourcepack.playerplushies;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// Tiers not final
public enum Pose {
	STANDING(Tier.TIER_1),
	WALKING(Tier.TIER_1),
	SITTING(Tier.TIER_2),
	DABBING(Tier.TIER_2),
	HANDSTAND(Tier.TIER_2),
	;

	private final Tier tier;

	public int getStartingIndex() {
		return ordinal() * 10000;
	}

}
