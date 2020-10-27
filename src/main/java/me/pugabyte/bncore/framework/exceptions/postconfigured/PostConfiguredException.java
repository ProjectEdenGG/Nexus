package me.pugabyte.bncore.framework.exceptions.postconfigured;

import me.pugabyte.bncore.framework.exceptions.BNException;
import net.md_5.bungee.api.ChatColor;

public class PostConfiguredException extends BNException {

	public PostConfiguredException(String message) {
		super(ChatColor.RED + message);
	}

}
