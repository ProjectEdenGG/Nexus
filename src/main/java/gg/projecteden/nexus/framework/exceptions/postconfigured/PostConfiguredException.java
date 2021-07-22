package gg.projecteden.nexus.framework.exceptions.postconfigured;

import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.utils.JsonBuilder;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;

public class PostConfiguredException extends NexusException {

	public PostConfiguredException(JsonBuilder json) {
		super(new JsonBuilder(NamedTextColor.RED).next(json));
	}

	public PostConfiguredException(ComponentLike component) {
		this(new JsonBuilder(component));
	}

	public PostConfiguredException(String message) {
		this(new JsonBuilder(message));
	}

}
