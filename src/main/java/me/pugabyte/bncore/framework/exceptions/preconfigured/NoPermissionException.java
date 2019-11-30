package me.pugabyte.bncore.framework.exceptions.preconfigured;

public class NoPermissionException extends PreConfiguredException {
	public NoPermissionException() {
		super("You don't have permission to do that!");
	}

}
