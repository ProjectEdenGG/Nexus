package me.pugabyte.nexus.framework.exceptions.postconfigured;

import me.pugabyte.nexus.utils.JsonBuilder;

public class InvalidInputException extends PostConfiguredException {

	public InvalidInputException(JsonBuilder json) {
		super(json);
	}

	public InvalidInputException(String message) {
		this(new JsonBuilder(message));
	}

}
