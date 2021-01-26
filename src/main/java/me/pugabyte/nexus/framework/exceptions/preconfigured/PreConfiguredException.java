package me.pugabyte.nexus.framework.exceptions.preconfigured;

import me.pugabyte.nexus.framework.exceptions.NexusException;
import net.md_5.bungee.api.ChatColor;

public class PreConfiguredException extends NexusException {
	public PreConfiguredException(String message) {
		super(ChatColor.RED + message);
	}
}
