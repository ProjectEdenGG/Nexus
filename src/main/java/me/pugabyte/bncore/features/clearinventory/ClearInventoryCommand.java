package me.pugabyte.bncore.features.clearinventory;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.clearinventory.models.ClearInventoryPlayer;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.bncore.features.clearinventory.ClearInventory.PREFIX;

public class ClearInventoryCommand implements CommandExecutor, TabCompleter {
	private final static String USAGE = "Correct usage: " + ChatColor.RED + "/%command% [undo]";

	ClearInventoryCommand() {
		BNCore.registerCommand("clearinventory", this);
		BNCore.registerTabCompleter("clearinventory", this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You must be in-game to run this command");
				return true;
			}

			Player player = (Player) sender;
			ClearInventoryPlayer ciPlayer = BNCore.clearInventory.getPlayer(player);

			if (args.length == 0) {
				ciPlayer.addCache();
				player.getInventory().setContents(new ItemStack[0]);
				player.sendMessage(PREFIX + "Inventory cleared");
			} else {
				if (args[0].equalsIgnoreCase("undo")) {
					ciPlayer.restoreCache();
				} else {
					throw new InvalidInputException(USAGE.replace("%command%", cmd.getName()));
				}
			}

			return true;
		} catch (InvalidInputException ex) {
			sender.sendMessage(PREFIX + ex.getMessage());
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		completions.add("undo");
		return completions;
	}
}
