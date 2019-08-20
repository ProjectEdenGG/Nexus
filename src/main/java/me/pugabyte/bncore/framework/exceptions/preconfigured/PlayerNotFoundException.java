package me.pugabyte.bncore.framework.exceptions.preconfigured;

public class PlayerNotFoundException extends PreConfiguredException {
	public PlayerNotFoundException() {
		super("Player not found");
	}

}