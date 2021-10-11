package gg.projecteden.nexus.features.resourcepack.playerplushies;

import gg.projecteden.nexus.features.store.Package;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Tier {
	TIER_1(Package.PLAYER_PLUSHIES_TIER_1),
	TIER_2(Package.PLAYER_PLUSHIES_TIER_1),
	TIER_3(Package.PLAYER_PLUSHIES_TIER_1),
	;

	private final Package storePackage;
}
