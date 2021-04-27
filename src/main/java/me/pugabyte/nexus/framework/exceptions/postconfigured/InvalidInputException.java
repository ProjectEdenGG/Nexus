package me.pugabyte.nexus.framework.exceptions.postconfigured;

import me.pugabyte.nexus.utils.JsonBuilder;
import net.kyori.adventure.text.ComponentLike;

public class InvalidInputException extends PostConfiguredException {

	public InvalidInputException(JsonBuilder json) {
		super(json);
	}

	public InvalidInputException(ComponentLike component) {
		this(new JsonBuilder(component));
	}

	public InvalidInputException(String message) {
		this(new JsonBuilder(message));
	}

}
