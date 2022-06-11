package gg.projecteden.nexus.features.minigames.models;

/**
 * Different types of messages sent in chat during minigames.
 */
public enum MinigameMessageType {
	/**
	 * Sent when a player joins a minigame.
	 */
	JOIN,
	/**
	 * Sent when a player leaves a minigame.
	 */
	QUIT,
	/**
	 * Sent when a player dies in a minigame.
	 */
	DEATH
}
