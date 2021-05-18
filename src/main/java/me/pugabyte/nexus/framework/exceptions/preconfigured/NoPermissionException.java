package me.pugabyte.nexus.framework.exceptions.preconfigured;

import me.pugabyte.nexus.utils.StringUtils;

public class NoPermissionException extends PreConfiguredException {

	public NoPermissionException() {
		this(null);
	}

	public NoPermissionException(String extra) {
		super("You don't have permission to do that!" + (StringUtils.isNullOrEmpty(extra) ? "" : " " + extra));
	}

}
