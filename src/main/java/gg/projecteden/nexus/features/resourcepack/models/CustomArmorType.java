package gg.projecteden.nexus.features.resourcepack.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomArmorType {
	WITHER(1),
	WARDEN(2),
	BERSERKER(3),
	BROWN_BERSERK(4),
	COPPER(5),
	DAMASCUS(6),
	DRUID(7),
	HELLFIRE(8),
	JARL(9),
	MYTHRIL(10),
	TANK(11),
	THOR(12),
	WIZARD(13),
	WOLF(14),
	;

	private final int id;
}
