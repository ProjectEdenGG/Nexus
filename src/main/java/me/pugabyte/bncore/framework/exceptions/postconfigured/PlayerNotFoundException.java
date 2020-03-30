package me.pugabyte.bncore.framework.exceptions.postconfigured;

import me.pugabyte.bncore.framework.exceptions.preconfigured.PreConfiguredException;

public class PlayerNotFoundException extends PreConfiguredException {

	public PlayerNotFoundException(String input) {
		super("Player &e" + input + " &cnot found");
	}

}