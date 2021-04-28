package me.pugabyte.nexus.features.regionapi;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Describes the way how an entity left/entered a region
 */
@Getter
@AllArgsConstructor
public enum MovementType {
	MOVE(true),
	RIDE(false),
	TELEPORT(true),
	WORLD_CHANGE(false),
	SPAWN(true),
	RESPAWN(false),
	DISCONNECT(false);

	private final boolean cancellable;

}
