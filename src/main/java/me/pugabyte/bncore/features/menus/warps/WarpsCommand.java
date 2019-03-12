package me.pugabyte.bncore.features.menus.warps;

import me.pugabyte.bncore.BNCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpsCommand implements CommandExecutor {

	public WarpsCommand() {
		BNCore.registerCommand("warps", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		WarpsMenu.open((Player) sender, WarpMenu.MAIN);
		return true;
	}
}
