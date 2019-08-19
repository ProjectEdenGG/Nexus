package me.pugabyte.bncore.models.exceptions.preconfigured;

public class MustBeCommandBlockException extends PreConfiguredException {

	public MustBeCommandBlockException() {
		super("You must be console to use this command");
	}

}
