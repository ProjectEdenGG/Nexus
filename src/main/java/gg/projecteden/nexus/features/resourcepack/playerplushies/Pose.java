package gg.projecteden.nexus.features.resourcepack.playerplushies;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// Tiers not final
public enum Pose {
	SITTING(Tier.TIER_1),
	STANDING(Tier.TIER_1),
	DABBING(Tier.TIER_2),
	HANDSTAND(Tier.TIER_2),
	;

	private final Tier tier;

	public int getStartingIndex() {
		return ordinal() * 10000;
	}

}
