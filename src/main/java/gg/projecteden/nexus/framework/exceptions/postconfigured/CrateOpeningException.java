package gg.projecteden.nexus.framework.exceptions.postconfigured;

public class CrateOpeningException extends PostConfiguredException {

	/**
	 * Used to full stop a crate process at any point and reset it.
	 *
	 * @param message The message to send to the player
	 */
	public CrateOpeningException(String message) {
		super(message);
	}

}
