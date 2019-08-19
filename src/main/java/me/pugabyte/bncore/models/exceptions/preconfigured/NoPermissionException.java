package me.pugabyte.bncore.models.exceptions.preconfigured;

public class NoPermissionException extends PreConfiguredException {
	public NoPermissionException() {
		super("You don't have permission to do that!");
	}

}
