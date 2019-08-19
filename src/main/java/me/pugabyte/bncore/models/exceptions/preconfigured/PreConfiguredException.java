package me.pugabyte.bncore.models.exceptions.preconfigured;

import me.pugabyte.bncore.models.exceptions.BNException;
import org.bukkit.ChatColor;

public class PreConfiguredException extends BNException {
	public PreConfiguredException(String message) {
		super(ChatColor.RED + message);
	}
}
