package me.pugabyte.nexus.features.regionapi;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Describes the way how an entity left/entered a region
 */
@Getter
@AllArgsConstructor
public enum MovementType {
	MOVE,
	RIDE,
	TELEPORT,
	WORLD_CHANGE,
	BED_ENTER,
	VEHICLE_ENTER,
	SPAWN,
	RESPAWN,
	DISCONNECT,
	;

}
