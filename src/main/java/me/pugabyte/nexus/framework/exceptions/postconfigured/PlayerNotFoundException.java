package me.pugabyte.nexus.framework.exceptions.postconfigured;

import me.pugabyte.nexus.framework.exceptions.preconfigured.PreConfiguredException;

public class PlayerNotFoundException extends PreConfiguredException {

	public PlayerNotFoundException(String input) {
		super("Player &e" + input + " &cnot found");
	}

}