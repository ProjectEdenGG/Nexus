package me.pugabyte.bncore.features.menus.itemeditor;

import me.pugabyte.bncore.BNCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ItemEditorCommand implements CommandExecutor {

	ItemEditorCommand() {
		BNCore.registerCommand("itemeditor", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if (!player.hasPermission("itemeditor.use")) {
			player.sendMessage(ChatColor.RED + "Permission denied");
			return false;
		} else {
			ItemEditorMenu.openItemEditor(player, ItemEditMenu.MAIN);
		}

		return true;
	}
}
