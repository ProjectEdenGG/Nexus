package gg.projecteden.nexus.features.resourcepack.playerplushies;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// Tiers not final
public enum Pose {
	STANDING(Tier.TIER_1),
	WALKING(Tier.TIER_1),
	T_POSE(Tier.TIER_1),
	HANDSTAND(Tier.TIER_1),

	SITTING(Tier.TIER_2),
	DABBING(Tier.TIER_2),
	RIDING_MINECART(Tier.TIER_2),
	HOLDING_GLOBE(Tier.TIER_2),

	WAVING(Tier.TIER_3),
	FUNKO_POP(Tier.TIER_3),

	FUNKO_POP_ADMIN(Tier.SERVER),
	FUNKO_POP_OWNER(Tier.SERVER),
	;

	private final Tier tier;

	public int getStartingIndex() {
		return ordinal() * 10000;
	}

}
