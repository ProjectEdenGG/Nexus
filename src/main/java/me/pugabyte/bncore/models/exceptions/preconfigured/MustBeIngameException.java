package me.pugabyte.bncore.models.exceptions.preconfigured;

public class MustBeIngameException extends PreConfiguredException {
	public MustBeIngameException() {
		super("You must be in-game to use this command");
	}

}
