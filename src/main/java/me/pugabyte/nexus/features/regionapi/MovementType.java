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
	ENTER_BED,
	ENTER_VEHICLE,
	SPAWN,
	RESPAWN,
	DESPAWN,
	CONNECT,
	DISCONNECT,
	;

}
