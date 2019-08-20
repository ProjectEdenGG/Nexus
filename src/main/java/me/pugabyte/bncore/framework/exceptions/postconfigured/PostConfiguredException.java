package me.pugabyte.bncore.framework.exceptions.postconfigured;

import me.pugabyte.bncore.framework.exceptions.BNException;
import org.bukkit.ChatColor;

public class PostConfiguredException extends BNException {

	public PostConfiguredException(String message) {
		super(ChatColor.RED + message);
	}

}
