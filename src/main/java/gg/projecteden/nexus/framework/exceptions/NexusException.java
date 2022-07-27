package gg.projecteden.nexus.framework.exceptions;

import gg.projecteden.api.common.exceptions.EdenException;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
