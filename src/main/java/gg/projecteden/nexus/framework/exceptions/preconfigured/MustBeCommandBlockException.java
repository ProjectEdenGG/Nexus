package gg.projecteden.nexus.framework.exceptions.preconfigured;

public class MustBeCommandBlockException extends PreConfiguredException {

	public MustBeCommandBlockException() {
		super("You must be a command block to use this command");
	}

}
