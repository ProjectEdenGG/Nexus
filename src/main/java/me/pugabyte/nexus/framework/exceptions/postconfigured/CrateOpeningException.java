package me.pugabyte.nexus.framework.exceptions.postconfigured;

import me.pugabyte.nexus.features.crates.models.Crate;

public class CrateOpeningException extends PostConfiguredException {

	/**
	 * Used the full stop a crate process at any point and reset it.
	 * The main use of this right now is checking for empty reward lists,
	 * but can be used at any point if a full stop is needed.
	 *
	 * @param message The message to send to the player
	 * @see Crate#pickCrateLoot()
	 */
	public CrateOpeningException(String message) {
		super(message);
	}

}
