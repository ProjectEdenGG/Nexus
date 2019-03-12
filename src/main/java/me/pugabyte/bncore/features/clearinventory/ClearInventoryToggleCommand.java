package me.pugabyte.bncore.features.clearinventory;

import me.pugabyte.bncore.BNCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static me.pugabyte.bncore.features.clearinventory.ClearInventory.PREFIX;

public class ClearInventoryToggleCommand implements CommandExecutor {

	ClearInventoryToggleCommand() {
		BNCore.registerCommand("clearinventorytoggle", this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage(PREFIX + "Use " + ChatColor.RED + "/ci undo " + ChatColor.DARK_AQUA + "to revert an inventoryContents clear");
		return true;
	}
}
