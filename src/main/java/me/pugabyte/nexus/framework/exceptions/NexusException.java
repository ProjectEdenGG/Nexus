package me.pugabyte.nexus.framework.exceptions;

import eden.exceptions.EdenException;
import lombok.Data;
import me.pugabyte.nexus.utils.JsonBuilder;

@Data
public class NexusException extends EdenException {
	private JsonBuilder json;

	public NexusException(JsonBuilder json) {
		super(json.toString());
		this.json = json;
	}

	public NexusException(String message) {
		this(new JsonBuilder(message));
	}

}
