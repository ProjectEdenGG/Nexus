package gg.projecteden.nexus.framework.exceptions.preconfigured;

/**
 * Thrown to indicate that a user tried to use a command or feature which is unavailable
 * while playing minigames.
 */
public class BlockedInMinigamesException extends PreConfiguredException {
	private static final String PLAY_ERROR = "This command cannot be used while playing a minigame";
	private static final String WORLD_ERROR = "This command cannot be used while in a game world";

	/**
	 * Constructs a new exception which indicates that a feature is unavailable in minigames.
	 * @param isWorld whether the feature is blocked in all game worlds ({@code true}) or
	 *                only blocked while playing a minigame ({@code false})
	 */
	public BlockedInMinigamesException(boolean isWorld) {
		super("This command cannot be used while playing a minigame");
	}
}
