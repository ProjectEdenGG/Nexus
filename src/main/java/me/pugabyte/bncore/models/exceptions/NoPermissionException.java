package me.pugabyte.bncore.models.exceptions;

import org.bukkit.ChatColor;

public class NoPermissionException extends Exception {

	public NoPermissionException() {
		super(ChatColor.RED + "You don't have permission to do that!");
	}
}
