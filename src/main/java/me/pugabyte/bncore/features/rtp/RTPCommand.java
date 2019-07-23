package me.pugabyte.bncore.features.rtp;

import me.pugabyte.bncore.BNCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RTPCommand implements CommandExecutor {
	public RTPCommand() {
		BNCore.registerCommand("jrtp", this);
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		return false;
	}

}
