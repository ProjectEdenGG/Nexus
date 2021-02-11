package me.pugabyte.nexus.framework.exceptions;

import lombok.Data;
import me.pugabyte.nexus.utils.JsonBuilder;

@Data
public class NexusException extends RuntimeException {
	private JsonBuilder json;

	public NexusException(JsonBuilder json) {
		super(json.toString());
		this.json = json;
	}

	public NexusException(String message) {
		this(new JsonBuilder(message));
	}

}
