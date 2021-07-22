package gg.projecteden.nexus.framework.exceptions.preconfigured;

import gg.projecteden.nexus.framework.exceptions.NexusException;
import net.md_5.bungee.api.ChatColor;

public class PreConfiguredException extends NexusException {

	public PreConfiguredException(String message) {
		super(ChatColor.RED + message);
	}
}
