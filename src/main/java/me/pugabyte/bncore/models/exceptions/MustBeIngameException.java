package me.pugabyte.bncore.models.exceptions;

import org.bukkit.ChatColor;

public class MustBeIngameException extends Exception {
	public MustBeIngameException() {
		super(ChatColor.RED + "You must be in-game to use this command");
	}

}
