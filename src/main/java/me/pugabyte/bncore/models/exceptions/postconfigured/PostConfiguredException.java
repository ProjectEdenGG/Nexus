package me.pugabyte.bncore.models.exceptions.postconfigured;

import me.pugabyte.bncore.models.exceptions.BNException;
import org.bukkit.ChatColor;

public class PostConfiguredException extends BNException {

	public PostConfiguredException(String message) {
		super(ChatColor.RED + message);
	}

}
