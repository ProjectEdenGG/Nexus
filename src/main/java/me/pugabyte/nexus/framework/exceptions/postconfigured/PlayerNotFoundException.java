package me.pugabyte.nexus.framework.exceptions.postconfigured;

public class PlayerNotFoundException extends eden.exceptions.postconfigured.PlayerNotFoundException {

	public PlayerNotFoundException(String input) {
		super("Player &e" + input + " &cnot found");
	}

}