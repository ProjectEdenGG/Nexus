package me.pugabyte.nexus.framework.exceptions.postconfigured;

import me.pugabyte.nexus.framework.exceptions.NexusException;
import net.md_5.bungee.api.ChatColor;

public class PostConfiguredException extends NexusException {

	public PostConfiguredException(String message) {
		super(ChatColor.RED + message);
	}

}
