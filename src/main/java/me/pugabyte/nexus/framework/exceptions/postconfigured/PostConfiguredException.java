package me.pugabyte.nexus.framework.exceptions.postconfigured;

import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.utils.JsonBuilder;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;

public class PostConfiguredException extends NexusException {

	public PostConfiguredException(JsonBuilder json) {
		super(json.color(NamedTextColor.RED));
	}

	public PostConfiguredException(ComponentLike component) {
		this(new JsonBuilder(component));
	}

	public PostConfiguredException(String message) {
		this(new JsonBuilder(message));
	}

}
