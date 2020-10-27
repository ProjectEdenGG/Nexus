package me.pugabyte.bncore.framework.exceptions.preconfigured;

import me.pugabyte.bncore.framework.exceptions.BNException;
import net.md_5.bungee.api.ChatColor;

public class PreConfiguredException extends BNException {
	public PreConfiguredException(String message) {
		super(ChatColor.RED + message);
	}
}
