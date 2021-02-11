package me.pugabyte.nexus.framework.exceptions.postconfigured;

import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.utils.JsonBuilder;
import net.md_5.bungee.api.ChatColor;

public class PostConfiguredException extends NexusException {

	public PostConfiguredException(JsonBuilder json) {
		super(new JsonBuilder(ChatColor.RED.toString()).next(json));
	}

	public PostConfiguredException(String message) {
		this(new JsonBuilder(message));
	}

}
