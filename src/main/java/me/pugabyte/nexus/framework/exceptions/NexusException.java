package me.pugabyte.nexus.framework.exceptions;

import eden.exceptions.EdenException;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.utils.JsonBuilder;
import net.kyori.adventure.text.ComponentLike;

@Data
@NoArgsConstructor
public class NexusException extends EdenException {
	private JsonBuilder json;

	public NexusException(JsonBuilder json) {
		super(json.toString());
		this.json = json;
	}

	public NexusException(ComponentLike component) {
		this(new JsonBuilder(component));
	}

	public NexusException(String message) {
		this(new JsonBuilder(message));
	}

	public ComponentLike withPrefix(String prefix) {
		return new JsonBuilder(prefix).next(getJson());
	}

}
