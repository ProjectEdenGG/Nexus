package gg.projecteden.nexus.framework.exceptions.preconfigured;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

public class NoPermissionException extends PreConfiguredException {

	public NoPermissionException() {
		this(null);
	}

	public NoPermissionException(String extra) {
		super("You don't have permission to do that!" + (isNullOrEmpty(extra) ? "" : " " + extra));
	}

}
